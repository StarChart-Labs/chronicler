/*
 * Copyright 2019 StarChart-Labs Contributors (https://github.com/StarChart-Labs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.starchartlabs.chronicler.github.model.pullrequest;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

import org.starchartlabs.calamari.core.MediaTypes;
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
