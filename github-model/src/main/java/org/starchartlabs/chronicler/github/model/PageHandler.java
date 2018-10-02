/*
 * Copyright (c) Oct 2, 2018 StarChart Labs Authors.
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
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.starchartlabs.chronicler.calamari.core.MediaTypes;
import org.starchartlabs.chronicler.calamari.core.PagingLinks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PageHandler {

    private final Gson gson;

    private final OkHttpClient httpClient;

    private final Supplier<String> authorizationHeader;

    // TODO romeara - allow providing a failure code handler that accepts the HTTP code?
    public PageHandler(Supplier<String> authorizationHeader) {
        this.authorizationHeader = Objects.requireNonNull(authorizationHeader);

        httpClient = new OkHttpClient();
        gson = new GsonBuilder().create();
    }

    // TODO Generalize, add other methods to just get data and such, and add to calamari? De-couple to allow use of
    // spring or other clients?
    public <T> Optional<T> pageUntil(String url, Function<JsonElement, T> toResult, Predicate<T> endCondition,
            BinaryOperator<T> reducer) throws IOException {
        HttpUrl nextUrl = HttpUrl.get(url);

        // First request, which initializes the paging links
        Response response = get(nextUrl);

        PagingLinks pagingLinks = new PagingLinks(response.headers("Link"));
        Optional<T> result = getBody(response, toResult, reducer);

        while (!result.map(endCondition::test).orElse(false)
                && (!pagingLinks.isLastPage(url.toString()) && pagingLinks.getNextPageUrl().isPresent())) {
            nextUrl = HttpUrl.get(pagingLinks.getNextPageUrl().get());
            response = get(nextUrl);

            pagingLinks = new PagingLinks(response.headers("Link"));
            result = getBody(response, toResult, reducer);
        }

        return result;
    }

    private Response get(HttpUrl url) throws IOException {
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

    private <T> Optional<T> getBody(Response response, Function<JsonElement, T> toResult, BinaryOperator<T> reducer)
            throws IOException {
        JsonElement element = gson.fromJson(response.body().string(), JsonElement.class);

        return StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                .map(toResult)
                .collect(Collectors.reducing(reducer));
    }


}
