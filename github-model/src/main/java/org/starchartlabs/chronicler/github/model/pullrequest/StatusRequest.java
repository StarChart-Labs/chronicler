/*
 * Copyright (c) Sep 30, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.github.model.pullrequest;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//{
//    "state": "success", (error, failure, pending, or success)
//    "target_url": "https://example.com/build/status",
//    "description": "The build succeeded!",
//    "context": "continuous-integration/jenkins"
//  }
public class StatusRequest {

    // TODO common?
    private static final String MEDIA_TYPE = "application/vnd.github.machine-man-preview+json";

    private static final Gson GSON = new GsonBuilder().create();

    private final String state;

    private final String description;

    private final String context;

    public StatusRequest(String state, String description, String context) {
        this.state = Objects.requireNonNull(state);
        this.description = Objects.requireNonNull(description);
        this.context = Objects.requireNonNull(context);
    }

    public String getState() {
        return state;
    }

    public String getDescription() {
        return description;
    }

    public String getContext() {
        return context;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public void sendRequest(String statusesUrl, Supplier<String> authorizationHeader) {
        OkHttpClient httpClient = new OkHttpClient();

        HttpUrl url = HttpUrl.get(statusesUrl);

        Request request = new Request.Builder()
                .method("POST", RequestBody.create(MediaType.get(MEDIA_TYPE), toJson()))
                .header("Authorization", authorizationHeader.get())
                .header("Accept", MEDIA_TYPE)
                .url(url)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to POST status (" + response.code() + ")");
            }
        } catch (IOException e) {
            // TODO romeara Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getState(),
                getDescription(),
                getContext());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof StatusRequest) {
            StatusRequest compare = (StatusRequest) obj;

            result = Objects.equals(compare.getState(), getState())
                    && Objects.equals(compare.getDescription(), getDescription())
                    && Objects.equals(compare.getContext(), getContext());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("state", getState())
                .add("description", getDescription())
                .add("context", getContext())
                .toString();
    }

}
