/*
 * Copyright (c) Oct 3, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.calamari.core.paging;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.chronicler.calamari.core.MediaTypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//TODO romeara move to calamari?
//TODO romeara - possibly re-implement as a spliterator implementation so real streams can be used?
public class PageReader {

    private final Supplier<String> authorizationHeader;

    private final String userAgent;

    public PageReader(Supplier<String> authorizationHeader, String userAgent) {
        this.authorizationHeader = Objects.requireNonNull(authorizationHeader);
        this.userAgent = Objects.requireNonNull(userAgent);
    }

    public PageStream<JsonElement> page(String url) {
        return new PageStream<>(url, userAgent, Function.identity(), authorizationHeader);
    }

    public static class PageStream<T> {

        /** Logger reference to output information to the application log files */
        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final String url;

        private final String userAgent;

        private final Function<JsonElement, T> mapper;

        private final Supplier<String> authorizationHeader;

        protected PageStream(String url, String userAgent, Function<JsonElement, T> mapper,
                Supplier<String> authorizationHeader) {
            this.url = Objects.requireNonNull(url);
            this.userAgent = Objects.requireNonNull(userAgent);
            this.mapper = Objects.requireNonNull(mapper);
            this.authorizationHeader = Objects.requireNonNull(authorizationHeader);
        }

        public <S> PageStream<S> map(Function<T, S> mapper) {
            return new PageStream<>(url, userAgent, this.mapper.andThen(mapper), authorizationHeader);
        }

        // TODO try reducer-less design
        public <A, R> R all(Collector<? super T, A, R> collector) throws IOException {
            return getPages(collector, a -> false);
        }

        public <A, R> R until(Collector<? super T, A, R> collector, Predicate<R> until)
                throws IOException {
            return getPages(collector, until);
        }

        // TODO romeara separated like this to allow override, using other clients - should this be separated
        // differently? Should JsonElement also be extracted out?
        protected <A, R> R getPages(Collector<? super T, A, R> collector, Predicate<R> until) throws IOException {
            // TODO romeara - use the split collector to prevent need for reducer?
            Collector<? super T, A, A> ongoingCollector = Collector.of(collector.supplier(),
                    collector.accumulator(),
                    collector.combiner());
            Function<A, R> converter = collector.finisher();

            OkHttpClient httpClient = new OkHttpClient();
            Gson gson = new GsonBuilder().create();
            HttpUrl nextUrl = HttpUrl.get(url);
            int pagesRead = 0;

            A combinedResult = null;
            A currentPageResult = null;

            // First request, which initializes the paging links
            Response response = getResponse(httpClient, nextUrl, authorizationHeader);

            PagingLinks pagingLinks = new PagingLinks(nextUrl.toString(), response.headers("Link"));
            currentPageResult = getBody(gson, response.body().string(), mapper, ongoingCollector);
            combinedResult = currentPageResult;

            while (!until.test(converter.apply(currentPageResult)) && pagingLinks.getNextPageUrl().isPresent()) {
                nextUrl = HttpUrl.get(pagingLinks.getNextPageUrl().get());
                response = getResponse(httpClient, nextUrl, authorizationHeader);

                pagingLinks = new PagingLinks(nextUrl.toString(), response.headers("Link"));
                currentPageResult = getBody(gson, response.body().string(), mapper, ongoingCollector);
                pagesRead++;

                combinedResult = ongoingCollector.combiner().apply(combinedResult, currentPageResult);
            }

            // TODO romeara reduce logging level once confirmed working
            logger.info("Read {} pages", pagesRead);

            return converter.apply(combinedResult);
        }

        private Response getResponse(OkHttpClient httpClient, HttpUrl url, Supplier<String> authorizationHeader)
                throws IOException {
            Request request = new Request.Builder()
                    .header("User-Agent", userAgent)
                    .get()
                    .header("Accept", MediaTypes.APP_PREVIEW)
                    .header("Authorization", authorizationHeader.get())
                    .url(url)
                    .build();

            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Error querying GitHub APIs");
            }

            return response;
        }

        private <R> R getBody(Gson gson, String responseBody, Function<JsonElement, T> mapper,
                Collector<? super T, ?, R> collector)
                        throws IOException {
            JsonElement element = gson.fromJson(responseBody, JsonElement.class);

            return StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                    .map(mapper)
                    .collect(collector);
        }

    }


}
