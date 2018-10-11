/*
 * Copyright (c) Oct 10, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.test.calamari.core.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.starchartlabs.chronicler.calamari.core.auth.ApplicationKey;
import org.starchartlabs.chronicler.calamari.core.auth.InstallationAccessToken;
import org.starchartlabs.chronicler.calamari.core.exception.KeyLoadingException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class InstallationAccessTokenTest {

    private static final Path TEST_RESOURCE_FOLDER = Paths.get("org", "starchartlabs", "chronicler", "test", "calamari",
            "core", "auth");

    private static final Gson GSON = new GsonBuilder().create();

    private ApplicationKey applicationKey;

    private String accessToken;

    private String accessTokenResponse;

    @BeforeClass
    public void setup() {
        applicationKey = new ApplicationKey("gitHubAppId", this::readPrivateKey);
        accessToken = "authorizationToken";

        JsonObject json = new JsonObject();
        json.addProperty("token", accessToken);

        accessTokenResponse = GSON.toJson(json);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullInstallationAccessTokenUrl() throws Exception {
        new InstallationAccessToken(null, applicationKey, "userAgent");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullApplicationKey() throws Exception {
        new InstallationAccessToken("http://url", null, "userAgent");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullUserAgent() throws Exception {
        new InstallationAccessToken("http://url", applicationKey, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void forRepositoryNullRepositoryUrl() throws Exception {
        InstallationAccessToken.forRepository(null, applicationKey, "userAgent");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void forRepositoryNullApplicationKey() throws Exception {
        InstallationAccessToken.forRepository("http://repo", null, "userAgent");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void forRepositoryNullUserAgent() throws Exception {
        InstallationAccessToken.forRepository("http://repo", applicationKey, null);
    }

    @Test(expectedExceptions = KeyLoadingException.class)
    public void getUnsuccessfulRequest() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(404));
            server.start();

            String installationAccessTokenUrl = server.url("/install").toString();

            InstallationAccessToken token = new InstallationAccessToken(installationAccessTokenUrl, applicationKey,
                    "userAgent");

            try {
                token.get();
            } finally {
                Assert.assertEquals(server.getRequestCount(), 1);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertNotNull(request.getHeader("Authorization"));
                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getPath(), "/install");
            }
        }
    }

    @Test
    public void get() throws Exception {
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(accessTokenResponse);

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(response);
            server.start();

            String installationAccessTokenUrl = server.url("/install").toString();

            InstallationAccessToken token = new InstallationAccessToken(installationAccessTokenUrl, applicationKey,
                    "userAgent");

            try {
                String result = token.get();

                Assert.assertNotNull(result);
                Assert.assertEquals(result, "token " + accessToken);
            } finally {
                Assert.assertEquals(server.getRequestCount(), 1);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertNotNull(request.getHeader("Authorization"));
                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getPath(), "/install");
            }
        }
    }

    @Test
    public void getCached() throws Exception {
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(accessTokenResponse);

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(response);
            server.start();

            String installationAccessTokenUrl = server.url("/install").toString();

            InstallationAccessToken token = new InstallationAccessToken(installationAccessTokenUrl, applicationKey,
                    "userAgent");

            try {
                String result = token.get();

                Assert.assertNotNull(result);
                Assert.assertEquals(result, "token " + accessToken);

                // Default caching is 59-60 minutes - this should not result in any further web calls
                for (int i = 0; i < 10; i++) {
                    token.get();
                }
            } finally {
                // Caching results in a single web call
                Assert.assertEquals(server.getRequestCount(), 1);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertNotNull(request.getHeader("Authorization"));
                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getPath(), "/install");
            }
        }
    }

    @Test
    public void forRepository() throws Exception {
        MockResponse accessResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(accessTokenResponse);

        try (MockWebServer server = new MockWebServer()) {
            server.start();
            String repoUrl = server.url("/repo").toString();
            String installationAccessTokenUrl = server.url("/install").toString();

            MockResponse respositoryResponse = new MockResponse()
                    .addHeader("Content-Type", "application/json")
                    .setBody(getRepositoryResponse(installationAccessTokenUrl));

            server.enqueue(respositoryResponse);
            server.enqueue(accessResponse);


            InstallationAccessToken token = InstallationAccessToken.forRepository(repoUrl, applicationKey,
                    "userAgent");

            try {
                String result = token.get();

                Assert.assertNotNull(result);
                Assert.assertEquals(result, "token " + accessToken);
            } finally {
                Assert.assertEquals(server.getRequestCount(), 2);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertNotNull(request.getHeader("Authorization"));
                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getPath(), "/repo/installation");

                request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertNotNull(request.getHeader("Authorization"));
                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getPath(), "/install");
            }
        }
    }

    private String getRepositoryResponse(String installationUrl) {
        JsonObject json = new JsonObject();
        json.addProperty("access_tokens_url", installationUrl);

        return GSON.toJson(json);
    }

    private String readPrivateKey() {
        // Note: The test key was generated from a GitHub App, and immediately removed as a valid key, and so is not a
        // security issue
        try (BufferedReader reader = getClasspathReader(
                TEST_RESOURCE_FOLDER.resolve("orphaned-github-private-key.pem"))) {
            String key = reader.lines()
                    .collect(Collectors.joining("\n"));
            System.out.println(key);
            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader getClasspathReader(Path filePath) {
        return new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath.toString()),
                        StandardCharsets.UTF_8));
    }

}
