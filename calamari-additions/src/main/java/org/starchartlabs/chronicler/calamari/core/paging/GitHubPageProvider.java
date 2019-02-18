/*
 * Copyright (c) Feb 13, 2019 StarChart Labs Authors.
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
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.starchartlabs.alloy.core.collections.PageIterator;
import org.starchartlabs.chronicler.calamari.core.MediaTypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GitHubPageProvider<T> implements PageIterator<T> {

    private final Supplier<String> authorizationHeader;

    private final String userAgent;

    private final JsonArrayConverter<?, T> itemMapper;

    private final OkHttpClient httpClient;

    private String url;

    public GitHubPageProvider(String url, Supplier<String> authorizationHeader, String userAgent,
            Function<String, Collection<T>> jsonDeserializer) {
        this(url, authorizationHeader, userAgent, new JsonArrayConverter<>(jsonDeserializer, Function.identity()));
    }

    private GitHubPageProvider(String url, Supplier<String> authorizationHeader, String userAgent,
            JsonArrayConverter<?, T> itemMapper) {
        this.authorizationHeader = Objects.requireNonNull(authorizationHeader);
        this.userAgent = Objects.requireNonNull(userAgent);
        this.url = Objects.requireNonNull(url);
        this.itemMapper = Objects.requireNonNull(itemMapper);

        httpClient = new OkHttpClient();
    }

    public <S> GitHubPageProvider<S> map(Function<T, S> mapperPerElement) {
        Objects.requireNonNull(mapperPerElement);

        return new GitHubPageProvider<>(url, authorizationHeader, userAgent, itemMapper.andThenEach(mapperPerElement));
    }

    @Override
    public boolean hasNext() {
        return url != null;
    }

    @Override
    public Collection<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        try {
            // Populate next set of elements, if possible
            Response response = getResponse(url);

            // Update tracking of paging position (URL and paging links)
            PagingLinks pagingLinks = new PagingLinks(response.headers("Link"));
            url = pagingLinks.getNextPageUrl().orElse(null);

            // Update cache of previously read elements, which will be read from until the next page is needed
            try (ResponseBody responseBody = response.body()) {
                return itemMapper.apply(responseBody.string());
            }
        } catch (IOException e) {
            // TODO romeara Specialized exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageIterator<T> trySplit() {
        // TODO romeara Can we find a way to do with page numbers?
        return null;
    }

    @Override
    public long estimateSize() {
        // TODO romeara Can we use page numbers to estimate this?
        return Long.MAX_VALUE;
    }

    public static GitHubPageProvider<JsonElement> gson(String url, Supplier<String> authorizationHeader,
            String userAgent) {
        return new GitHubPageProvider<>(url, authorizationHeader, userAgent, new JsonElementConverter());
    }

    private Response getResponse(String url) throws IOException {
        Request request = new Request.Builder()
                .header("User-Agent", userAgent)
                .get()
                .header("Accept", MediaTypes.APP_PREVIEW)
                .header("Authorization", authorizationHeader.get())
                .url(url)
                .build();

        Response response = httpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Error querying GitHub APIs (" + response.code() + ")");
        }

        return response;
    }

    // Allows packaging of multiple per-element functions for more efficient handling
    private static final class JsonArrayConverter<S, T> implements Function<String, Collection<T>> {

        private final Function<String, Collection<S>> jsonDeserializer;

        private final Function<S, T> mapperPerElement;

        public JsonArrayConverter(Function<String, Collection<S>> jsonDeserializer, Function<S, T> mapperPerElement) {
            this.jsonDeserializer = Objects.requireNonNull(jsonDeserializer);
            this.mapperPerElement = Objects.requireNonNull(mapperPerElement);
        }

        @Override
        public Collection<T> apply(String json) {
            return jsonDeserializer.apply(json).stream()
                    .map(mapperPerElement)
                    .collect(Collectors.toList());
        }

        public <U> JsonArrayConverter<S, U> andThenEach(Function<T, U> mapperPerElement) {
            return new JsonArrayConverter<>(jsonDeserializer, this.mapperPerElement.andThen(mapperPerElement));
        }

    }

    // TODO romeara Had to explicitly create to avoid creating a new GSON instance for each page call
    private static final class JsonElementConverter implements Function<String, Collection<JsonElement>> {

        private Gson gson;

        public JsonElementConverter() {
            gson = new GsonBuilder().create();
        }

        @Override
        public Collection<JsonElement> apply(String json) {
            JsonElement element = gson.fromJson(json, JsonElement.class);

            return StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                    .collect(Collectors.toList());
        }

    }

}