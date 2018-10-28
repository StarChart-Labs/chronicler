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
import org.starchartlabs.chronicler.calamari.core.exception.KeyLoadingException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Represents an access token used to validate web requests to GitHub as a
 * <a href="https://developer.github.com/v3/apps/installations/">GitHub App installation</a>
 *
 * <p>
 * Handles logic for exchanging an application key for an installation-specific access token and caching them until
 * invalid, as described in
 * <a href= "https://developer.github.com/apps/building-github-apps/authenticating-with-github-apps/">authenticating as
 * a GitHub App</a>
 *
 * <p>
 * Uses Java {@link Supplier} pattern to allow re-generation of tokens as needed
 *
 * @author romeara
 * @since 0.1.0
 */
public class InstallationAccessToken implements Supplier<String> {

    // The maximum is 60, allow for some drift
    private static final int EXPIRATION_MINUTES = 59;

    private static final Gson GSON = new GsonBuilder().create();

    private final ApplicationKey applicationKey;

    private final String installationAccessTokenUrl;

    private final String userAgent;

    private final OkHttpClient httpClient;

    private final Supplier<String> headerSupplier;

    /**
     * @param installationAccessTokenUrl
     *            URL which represents access token resources for a specific GitHub App installation
     * @param applicationKey
     *            Key used to access GitHub web resources as a GitHub App outside an installation context
     * @param userAgent
     *            The user agent to make web requests as, as
     *            <a href="https://developer.github.com/v3/#user-agent-required">required by GitHub</a>
     * @since 0.1.0
     */
    public InstallationAccessToken(String installationAccessTokenUrl, ApplicationKey applicationKey,
            String userAgent) {
        this.applicationKey = Objects.requireNonNull(applicationKey);
        this.installationAccessTokenUrl = Objects.requireNonNull(installationAccessTokenUrl);
        this.userAgent = Objects.requireNonNull(userAgent);

        httpClient = new OkHttpClient();
        headerSupplier = Suppliers.map(
                Suppliers.memoizeWithExpiration(this::generateNewToken, EXPIRATION_MINUTES, TimeUnit.MINUTES),
                InstallationAccessToken::toAuthorizationHeader);
    }

    /**
     * @return Authorization header value to authenticate as a GitHub App installation
     * @throws KeyLoadingException
     *             If the is an error making the GitHub web request to obtain the access token
     * @since 0.1.0
     */
    @Override
    public String get() {
        return headerSupplier.get();
    }

    /**
     * Generates a new access token from the application key reference and a known installation instance
     *
     * @return Generated access token valid for up to sixty minutes after this function is called
     * @throws KeyLoadingException
     *             If the is an error making the GitHub web request to obtain the access token
     */
    private String generateNewToken() {
        HttpUrl url = HttpUrl.parse(installationAccessTokenUrl);

        RequestBody body = RequestBody.create(null, new byte[] {});
        Request request = new Request.Builder()
                .post(body)
                .header("Authorization", applicationKey.get())
                .header("Accept", MediaTypes.APP_PREVIEW)
                .header("User-Agent", userAgent)
                .url(url)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                return AccessTokenResponse.fromJson(response.body().string()).getToken();
            } else {
                throw new KeyLoadingException(
                        Strings.format("Request exchanging application key for installation token failed (%s - %s)",
                                response.code(), response.message()));
            }
        } catch (IOException e) {
            throw new KeyLoadingException("Error requesting or deserializing GitHub installation token response", e);
        }
    }

    /**
     * Creates an installation access token for the installation on a given repository.
     *
     * <p>
     * Uses the provided {@code applicationKey} to read required installation details from GitHub specific to the
     * repository represented at the provided URL
     *
     * @param repositoryUrl
     *            The API URL which represents the target repository on GitHub
     * @param applicationKey
     *            Application key which allows authentication as a GitHub App in web requests
     * @param userAgent
     *            User agent to make repository requests with, as
     *            <a href="https://developer.github.com/v3/#user-agent-required">required by GitHub</a>
     * @return A reference to a renewable access token for authentication as a specific installation in web requests to
     *         GitHub
     * @since 0.1.0
     */
    public static InstallationAccessToken forRepository(String repositoryUrl, ApplicationKey applicationKey,
            String userAgent) {
        Objects.requireNonNull(applicationKey);
        Objects.requireNonNull(repositoryUrl);

        OkHttpClient httpClient = new OkHttpClient();

        HttpUrl url = HttpUrl.parse(repositoryUrl).newBuilder()
                .addEncodedPathSegment("installation")
                .build();

        Request request = new Request.Builder()
                .get()
                .header("Authorization", applicationKey.get())
                .header("Accept", MediaTypes.APP_PREVIEW)
                .header("User-Agent", userAgent)
                .url(url)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String installationAccessTokenUrl = InstallationResponse.fromJson(response.body().string())
                        .getAccessTokensUrl();

                return new InstallationAccessToken(installationAccessTokenUrl, applicationKey, userAgent);
            } else {
                throw new RuntimeException("Request unsuccessful (" + response.code() + ")");
            }
        } catch (IOException e) {
            throw new KeyLoadingException("Error requesting or deserializing GitHub installation response", e);
        }
    }

    /**
     * @param token
     *            Access token to use in requests to GitHub for authorization as a specific GitHub App installation
     * @return Value for the {@code Authorization} header of HTTP requests to authorize with the access token
     */
    private static String toAuthorizationHeader(String token) {
        Objects.requireNonNull(token);

        return Strings.format("token %s", token);
    }

    /**
     * Represents relevant parts of a JSON response from GitHub describing an App <a href=
     * "https://developer.github.com/apps/building-github-apps/authenticating-with-github-apps/#authenticating-as-an-installation">installation
     * access token</a>
     *
     * @author romeara
     */
    private static final class AccessTokenResponse {

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

    /**
     * Represents relevant parts of a JSON response from GitHub describing an App
     * <a href="https://developer.github.com/v3/apps/installations/">installation</a>
     *
     * @author romeara
     */
    private static final class InstallationResponse {

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
