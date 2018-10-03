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
package org.starchartlabs.chronicler.github.model;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.chronicler.calamari.core.MediaTypes;
import org.starchartlabs.chronicler.calamari.core.PagingLinks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PageReader {

    private final Supplier<String> authorizationHeader;

    public PageReader(Supplier<String> authorizationHeader) {
        this.authorizationHeader = Objects.requireNonNull(authorizationHeader);
    }

    public PageStream<JsonElement> page(String url) {
        return new PageStream<>(url, Function.identity(), authorizationHeader);
    }

    public static class PageStream<T> {

        /** Logger reference to output information to the application log files */
        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final String url;

        private final Function<JsonElement, T> mapper;

        private final Supplier<String> authorizationHeader;

        protected PageStream(String url, Function<JsonElement, T> mapper, Supplier<String> authorizationHeader) {
            this.url = Objects.requireNonNull(url);
            this.mapper = Objects.requireNonNull(mapper);
            this.authorizationHeader = Objects.requireNonNull(authorizationHeader);
        }

        public <S> PageStream<S> map(Function<T, S> mapper) {
            return new PageStream<>(url, this.mapper.andThen(mapper), authorizationHeader);
        }

        public <R> R all(Collector<? super T, ?, R> collector, BinaryOperator<R> reducer) throws IOException {
            return getPages(collector, reducer, a -> false);
        }

        public <R> R until(Collector<? super T, ?, R> collector, BinaryOperator<R> reducer, Predicate<R> until)
                throws IOException {
            return getPages(collector, reducer, until);
        }

        // TODO romeara separated like this to allow override, using other clients - should this be separated
        // differently? Should JsonElement also be extracted out?
        protected <R> R getPages(Collector<? super T, ?, R> collector, BinaryOperator<R> reducer, Predicate<R> until)
                throws IOException {
            OkHttpClient httpClient = new OkHttpClient();
            Gson gson = new GsonBuilder().create();
            HttpUrl nextUrl = HttpUrl.get(url);
            int pagesRead = 0;

            // First request, which initializes the paging links
            Response response = getResponse(httpClient, nextUrl, authorizationHeader);

            PagingLinks pagingLinks = new PagingLinks(response.headers("Link"));
            R result = getBody(gson, response.body().string(), mapper, collector);
            pagesRead++;

            while (!until.test(result) && pagingLinks.hasNextPage(nextUrl.toString())) {
                nextUrl = HttpUrl.get(pagingLinks.getNextPageUrl().get());
                response = getResponse(httpClient, nextUrl, authorizationHeader);

                pagingLinks = new PagingLinks(response.headers("Link"));
                R newResult = getBody(gson, response.body().string(), mapper, collector);
                pagesRead++;

                result = reducer.apply(result, newResult);
            }

            // TODO romeara reduce loggin level once comfirmed working
            logger.info("Read {} pages", pagesRead);

            return result;
        }

        private Response getResponse(OkHttpClient httpClient, HttpUrl url, Supplier<String> authorizationHeader)
                throws IOException {
            Request request = Requests.newRequest()
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
