/*
 * Copyright (c) Sep 23, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.calamari.core.auth;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.starchartlabs.alloy.core.Strings;
import org.starchartlabs.alloy.core.Suppliers;
import org.starchartlabs.chronicler.calamari.core.MediaTypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//https://developer.github.com/apps/building-github-apps/authenticating-with-github-apps/
public class InstallationAccessToken implements Supplier<String> {

    // The maximum is 60, allow for some drift
    private static final int EXPIRATION_MINUTES = 59;

    private final Supplier<String> applicationKeyHeaderSupplier;

    private final String installationAccessTokenUrl;

    private final String userAgent;

    private final OkHttpClient httpClient;

    private final Supplier<String> headerSupplier;

    public InstallationAccessToken(String installationAccessTokenUrl, Supplier<String> applicationKeyHeaderSupplier,
            String userAgent) {
        this.applicationKeyHeaderSupplier = Objects.requireNonNull(applicationKeyHeaderSupplier);
        this.installationAccessTokenUrl = Objects.requireNonNull(installationAccessTokenUrl);
        this.userAgent = Objects.requireNonNull(userAgent);

        httpClient = new OkHttpClient();
        headerSupplier = Suppliers.map(
                Suppliers.memoizeWithExpiration(this::generateNewToken, EXPIRATION_MINUTES, TimeUnit.MINUTES),
                InstallationAccessToken::toAuthorizationHeader);
    }

    @Override
    public String get() {
        return headerSupplier.get();
    }

    private String generateNewToken() {
        HttpUrl url = HttpUrl.parse(installationAccessTokenUrl);

        RequestBody body = RequestBody.create(null, new byte[] {});
        Request request = new Request.Builder()
                .post(body)
                .header("Authorization", applicationKeyHeaderSupplier.get())
                .header("Accept", MediaTypes.APP_PREVIEW)
                .header("User-Agent", userAgent)
                .url(url)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                return AccessTokenResponse.fromJson(response.body().string()).getToken();
            } else {
                throw new RuntimeException("Request unsuccessful (" + response.code() + ")");
            }
        } catch (IOException e) {
            // TODO romeara better way to deal wiht this?
            throw new RuntimeException("Error exchanging application key for access token", e);
        }
    }

    public static InstallationAccessToken forRepository(String repositoryUrl,
            Supplier<String> applicationKeyHeaderSupplier, String userAgent) {
        Objects.requireNonNull(applicationKeyHeaderSupplier);
        Objects.requireNonNull(repositoryUrl);
        Objects.requireNonNull(userAgent);

        OkHttpClient httpClient = new OkHttpClient();

        // access_tokens_url

        HttpUrl url = HttpUrl.parse(repositoryUrl).newBuilder()
                .addEncodedPathSegment("installation")
                .build();

        Request request = new Request.Builder()
                .get()
                .header("Authorization", applicationKeyHeaderSupplier.get())
                .header("Accept", MediaTypes.APP_PREVIEW)
                .header("User-Agent", userAgent)
                .url(url)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String installationAccessTokenUrl = InstallationResponse.fromJson(response.body().string())
                        .getAccessTokensUrl();

                return new InstallationAccessToken(installationAccessTokenUrl, applicationKeyHeaderSupplier, userAgent);
            } else {
                throw new RuntimeException("Request unsuccessful (" + response.code() + ")");
            }
        } catch (IOException e) {
            // TODO romeara better way to deal wiht this?
            throw new RuntimeException("Error exchanging application key for access token", e);
        }
    }

    private static String toAuthorizationHeader(String token) {
        Objects.requireNonNull(token);

        return Strings.format("token %s", token);
    }

    private static final class AccessTokenResponse {

        private static final Gson GSON = new GsonBuilder().create();

        private final String token;

        @SuppressWarnings("unused")
        public AccessTokenResponse(String token) {
            this.token = Objects.requireNonNull(token);
        }

        public String getToken() {
            return token;
        }

        public static AccessTokenResponse fromJson(String json) {
            return GSON.fromJson(json, AccessTokenResponse.class);
        }

    }

    private static final class InstallationResponse {

        private static final Gson GSON = new GsonBuilder().create();

        @SerializedName("access_tokens_url")
        private final String accessTokensUrl;

        @SuppressWarnings("unused")
        public InstallationResponse(String accessTokensUrl) {
            this.accessTokensUrl = Objects.requireNonNull(accessTokensUrl);
        }

        public String getAccessTokensUrl() {
            return accessTokensUrl;
        }

        public static InstallationResponse fromJson(String json) {
            return GSON.fromJson(json, InstallationResponse.class);
        }

    }

}
