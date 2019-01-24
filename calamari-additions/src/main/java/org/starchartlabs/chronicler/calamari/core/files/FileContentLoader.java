/*
 * Copyright (c) Jan 16, 2019 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.calamari.core.files;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.alloy.core.Preconditions;
import org.starchartlabs.alloy.core.Strings;
import org.starchartlabs.calamari.core.ResponseConditions;
import org.starchartlabs.calamari.core.auth.InstallationAccessToken;
import org.starchartlabs.chronicler.calamari.core.MediaTypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Represents handling for reading the contents of a file from a GitHub repository
 *
 * @author romeara
 * @since 0.2.0
 */
public class FileContentLoader {

    private static final Gson GSON = new GsonBuilder().create();

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final InstallationAccessToken installationToken;

    private final String userAgent;

    public FileContentLoader(InstallationAccessToken installationToken, String userAgent) {
        this.installationToken = Objects.requireNonNull(installationToken);
        this.userAgent = Objects.requireNonNull(userAgent);
    }

    // https://developer.github.com/v3/repos/contents/
    public Optional<String> loadContents(String repositoryUrl, String ref, String path) {
        Objects.requireNonNull(repositoryUrl);
        Objects.requireNonNull(ref);
        Objects.requireNonNull(path);

        String result = null;

        OkHttpClient httpClient = new OkHttpClient();

        HttpUrl url = HttpUrl.parse(repositoryUrl).newBuilder()
                .addEncodedPathSegment("contents")
                .addPathSegments(path)
                .addQueryParameter("ref", ref)
                .build();

        Request request = new Request.Builder()
                .get()
                .header("Authorization", installationToken.get())
                .header("Accept", MediaTypes.APP_PREVIEW)
                .header("User-Agent", userAgent)
                .url(url)
                .build();

        String responseBody = null;

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                try (ResponseBody body = response.body()) {
                    responseBody = body.string();
                    ContentResponse content = ContentResponse.fromJson(responseBody);

                    Preconditions.checkArgument(Objects.equals(content.getEncoding(), "base64"),
                            Strings.format("GitHub content responses are expected to be of base64 encoding, got %s",
                                    content.getEncoding()));

                    try {
                        result = new String(
                                Base64.getMimeDecoder().decode(content.getContent().getBytes(StandardCharsets.UTF_8)));
                    } catch (IllegalArgumentException e) {
                        logger.error("Error deserializing base64 from response: {}", responseBody);

                        throw e;
                    }
                }
            } else if (response.code() != 404) {
                ResponseConditions.checkRateLimit(response);

                throw new RuntimeException(
                        "Request unsuccessful (" + response.code() + " - " + response.message() + ")");
            }

            return Optional.ofNullable(result);
        } catch (IOException | JsonSyntaxException e) {
            logger.error("Error reading contents: {}", responseBody);

            throw new RuntimeException(
                    "Error requesting or deserializing GitHub file content response.", e);
        }
    }

    private static final class ContentResponse {

        @SerializedName("encoding")
        private final String encoding;

        @SerializedName("content")
        private final String content;

        @SuppressWarnings("unused")
        public ContentResponse(String encoding, String content) {
            this.encoding = Objects.requireNonNull(encoding);
            this.content = Objects.requireNonNull(content);
        }

        public String getEncoding() {
            return encoding;
        }

        public String getContent() {
            return content;
        }

        public static ContentResponse fromJson(String json) {
            return GSON.fromJson(json, ContentResponse.class);
        }

    }

}
