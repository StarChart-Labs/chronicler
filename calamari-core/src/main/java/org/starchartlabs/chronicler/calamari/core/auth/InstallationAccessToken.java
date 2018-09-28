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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//https://developer.github.com/apps/building-github-apps/authenticating-with-github-apps/
public class InstallationAccessToken {

    // TODO romeara make this overridable by ENV because likely to be changed by GitHub
    private static final String TOKEN_MEDIA_TYPE = "application/vnd.github.machine-man-preview+json";

    // The maximum is 60, allow for some drift
    private static final int EXPIRATION_MINUTES = 59;

    private final Supplier<String> applicationKeyHeaderSupplier;

    private final String installationAccessTokenUrl;

    private final OkHttpClient httpClient;

    public InstallationAccessToken(Supplier<String> applicationKeyHeaderSupplier, String installationAccessTokenUrl) {
        this.applicationKeyHeaderSupplier = Objects.requireNonNull(applicationKeyHeaderSupplier);
        this.installationAccessTokenUrl = Objects.requireNonNull(installationAccessTokenUrl);

        httpClient = new OkHttpClient();
    }

    public Supplier<String> getTokenHeaderSupplier() {
        return Suppliers.map(getTokenSupplier(), InstallationAccessToken::toAuthorizationHeader);
    }

    // TODO romeara Protected to allow access if needed, use case not certain for non-header value
    protected Supplier<String> getTokenSupplier() {
        return Suppliers.memoizeWithExpiration(this::generateNewToken, EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    private String generateNewToken() {
        HttpUrl url = HttpUrl.parse(installationAccessTokenUrl);

        RequestBody body = RequestBody.create(null, new byte[] {});
        Request request = new Request.Builder()
                .method("POST", body)
                .header("Authorization", applicationKeyHeaderSupplier.get())
                .header("Accept", TOKEN_MEDIA_TYPE)
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

    public static InstallationAccessToken forRepository(Supplier<String> applicationKeyHeaderSupplier,
            String repositoryUrl) {
        Objects.requireNonNull(applicationKeyHeaderSupplier);
        Objects.requireNonNull(repositoryUrl);

        OkHttpClient httpClient = new OkHttpClient();

        // access_tokens_url

        HttpUrl url = HttpUrl.parse(repositoryUrl).newBuilder()
                .addEncodedPathSegment("installation")
                .build();

        Request request = new Request.Builder()
                .method("GET", null)
                .header("Authorization", applicationKeyHeaderSupplier.get())
                .header("Accept", TOKEN_MEDIA_TYPE)
                .url(url)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String installationAccessTokenUrl = InstallationResponse.fromJson(response.body().string())
                        .getAccessTokensUrl();

                return new InstallationAccessToken(applicationKeyHeaderSupplier, installationAccessTokenUrl);
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
