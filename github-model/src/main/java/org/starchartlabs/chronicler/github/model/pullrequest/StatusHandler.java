/*
 * Copyright (c) Oct 1, 2018 StarChart Labs Authors.
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

import org.starchartlabs.chronicler.calamari.core.MediaTypes;
import org.starchartlabs.chronicler.github.model.Requests;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StatusHandler {

    private final String context;

    private final String statusesUrl;

    private final Supplier<String> authorizationHeader;

    public StatusHandler(String context, String statusesUrl, Supplier<String> authorizationHeader) {
        this.context = Objects.requireNonNull(context);
        this.statusesUrl = Objects.requireNonNull(statusesUrl);
        this.authorizationHeader = Objects.requireNonNull(authorizationHeader);
    }

    public void sendError(String description) {
        sendRequest(State.ERROR, description);
    }

    public void sendFailure(String description) {
        sendRequest(State.FAILURE, description);
    }

    public void sendPending(String description) {
        sendRequest(State.PENDING, description);
    }

    public void sendSuccess(String description) {
        sendRequest(State.SUCCESS, description);
    }

    public void sendRequest(State state, String description) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(description);

        StatusRequest statusRequest = new StatusRequest(state, description, context);
        OkHttpClient httpClient = new OkHttpClient();

        HttpUrl url = HttpUrl.get(statusesUrl);

        Request request = Requests.newRequest()
                .post(RequestBody.create(MediaType.get(MediaTypes.APP_PREVIEW), statusRequest.toJson()))
                .header("Authorization", authorizationHeader.get())
                .header("Accept", MediaTypes.APP_PREVIEW)
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

}
