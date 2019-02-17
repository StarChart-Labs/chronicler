/*
 * Copyright (c) Jan 21, 2019 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.test.calamari.core.files;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.starchartlabs.calamari.core.auth.InstallationAccessToken;
import org.starchartlabs.calamari.core.exception.RequestLimitExceededException;
import org.starchartlabs.chronicler.calamari.core.MediaTypes;
import org.starchartlabs.chronicler.calamari.core.files.ConfigurationFileLoader;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class FileContentLoaderTest {

    private static final String RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining";

    private static final Path TEST_RESOURCE_FOLDER = Paths.get("org", "starchartlabs", "chronicler", "test", "calamari",
            "core", "files");

    @Mock
    private InstallationAccessToken accessToken;

    private ConfigurationFileLoader fileContentLoader;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);

        fileContentLoader = new ConfigurationFileLoader("userAgent", "path.json");
    }

    @AfterMethod
    public void teardown() {
        Mockito.verifyNoMoreInteractions(accessToken);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullUserAgent() throws Exception {
        new ConfigurationFileLoader(null, "path.json");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullPath() throws Exception {
        new ConfigurationFileLoader("userAgent", null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void loadContentsNullAccessToken() throws Exception {
        fileContentLoader.loadContents(null, "repositoryUrl", "ref");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void loadContentsNullRepositoryUrl() throws Exception {
        fileContentLoader.loadContents(accessToken, null, "ref");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void loadContentsNullRef() throws Exception {
        fileContentLoader.loadContents(accessToken, "repositoryUrl", null);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void loadContentsErrorResponse() throws Exception {
        MockResponse response = new MockResponse()
                .setResponseCode(412);

        String owner = "owner";
        String repository = "repository";
        String ref = "ref";
        String path = "path.json";

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(response);
            server.start();

            String repositoryUrl = server.url("/api/repos/" + owner + "/" + repository).toString();

            ConfigurationFileLoader contentLoader = new ConfigurationFileLoader("userAgent", path);

            Mockito.when(accessToken.get()).thenReturn("token authToken12345");

            try {
                contentLoader.loadContents(accessToken, repositoryUrl, ref);
            } finally {
                Assert.assertEquals(server.getRequestCount(), 1);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getHeader("Accept"), MediaTypes.APP_PREVIEW);
                Assert.assertEquals(request.getHeader("Authorization"), "token authToken12345");
                Assert.assertEquals(request.getPath(),
                        "/api/repos/" + owner + "/" + repository + "/contents/" + path + "?ref=" + ref);

                Mockito.verify(accessToken).get();
            }
        }
    }

    @Test(expectedExceptions = RequestLimitExceededException.class)
    public void loadContentsRateLimitExceeded() throws Exception {
        MockResponse response = new MockResponse()
                .setResponseCode(403)
                .addHeader(RATE_LIMIT_REMAINING_HEADER, "0");

        String owner = "owner";
        String repository = "repository";
        String ref = "ref";
        String path = "path.json";

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(response);
            server.start();

            String repositoryUrl = server.url("/api/repos/" + owner + "/" + repository).toString();

            ConfigurationFileLoader contentLoader = new ConfigurationFileLoader("userAgent", path);

            Mockito.when(accessToken.get()).thenReturn("token authToken12345");

            try {
                contentLoader.loadContents(accessToken, repositoryUrl, ref);
            } finally {
                Assert.assertEquals(server.getRequestCount(), 1);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getHeader("Accept"), MediaTypes.APP_PREVIEW);
                Assert.assertEquals(request.getHeader("Authorization"), "token authToken12345");
                Assert.assertEquals(request.getPath(),
                        "/api/repos/" + owner + "/" + repository + "/contents/" + path + "?ref=" + ref);

                Mockito.verify(accessToken).get();
            }
        }
    }

    @Test
    public void loadContentsNotFound() throws Exception {
        MockResponse response = new MockResponse()
                .setResponseCode(404);

        String owner = "owner";
        String repository = "repository";
        String ref = "ref";
        String path = "path.json";

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(response);
            server.start();

            String repositoryUrl = server.url("/api/repos/" + owner + "/" + repository).toString();

            ConfigurationFileLoader contentLoader = new ConfigurationFileLoader("userAgent", path);

            Mockito.when(accessToken.get()).thenReturn("token authToken12345");

            try {
                Optional<String> result = contentLoader.loadContents(accessToken, repositoryUrl, ref);

                Assert.assertNotNull(result);
                Assert.assertFalse(result.isPresent());
            } finally {
                Assert.assertEquals(server.getRequestCount(), 1);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getHeader("Accept"), MediaTypes.APP_PREVIEW);
                Assert.assertEquals(request.getHeader("Authorization"), "token authToken12345");
                Assert.assertEquals(request.getPath(),
                        "/api/repos/" + owner + "/" + repository + "/contents/" + path + "?ref=" + ref);

                Mockito.verify(accessToken).get();
            }
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void loadContentUnexpectedEncoding() throws Exception {
        String responseJson = null;

        try (BufferedReader reader = getClasspathReader(
                TEST_RESOURCE_FOLDER.resolve("fileContentUnsupportedEncodingResponse.json"))) {
            responseJson = reader.lines()
                    .collect(Collectors.joining("\n"));
        }

        MockResponse response = new MockResponse()
                .addHeader("Content-Type", MediaTypes.APP_PREVIEW)
                .setBody(responseJson);

        String owner = "owner";
        String repository = "repository";
        String ref = "ref";
        String path = "path.json";

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(response);
            server.start();

            String repositoryUrl = server.url("/api/repos/" + owner + "/" + repository).toString();

            ConfigurationFileLoader contentLoader = new ConfigurationFileLoader("userAgent", path);

            Mockito.when(accessToken.get()).thenReturn("token authToken12345");

            try {
                contentLoader.loadContents(accessToken, repositoryUrl, ref);
            } finally {
                Assert.assertEquals(server.getRequestCount(), 1);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getHeader("Accept"), MediaTypes.APP_PREVIEW);
                Assert.assertEquals(request.getHeader("Authorization"), "token authToken12345");
                Assert.assertEquals(request.getPath(),
                        "/api/repos/" + owner + "/" + repository + "/contents/" + path + "?ref=" + ref);

                Mockito.verify(accessToken).get();
            }
        }
    }

    @Test
    public void loadContents() throws Exception {
        String responseJson = null;

        try (BufferedReader reader = getClasspathReader(TEST_RESOURCE_FOLDER.resolve("fileContentResponse.json"))) {
            responseJson = reader.lines()
                    .collect(Collectors.joining("\n"));
        }

        MockResponse response = new MockResponse()
                .addHeader("Content-Type", MediaTypes.APP_PREVIEW)
                .setBody(responseJson);

        String owner = "owner";
        String repository = "repository";
        String ref = "ref";
        String path = "path.json";

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(response);
            server.start();

            String repositoryUrl = server.url("/api/repos/" + owner + "/" + repository).toString();

            ConfigurationFileLoader contentLoader = new ConfigurationFileLoader("userAgent", path);

            Mockito.when(accessToken.get()).thenReturn("token authToken12345");

            try {
                Optional<String> result = contentLoader.loadContents(accessToken, repositoryUrl, ref);

                Assert.assertNotNull(result);
                Assert.assertTrue(result.isPresent());
                Assert.assertEquals(result.get(), "This is test text");
            } finally {
                Assert.assertEquals(server.getRequestCount(), 1);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getHeader("Accept"), MediaTypes.APP_PREVIEW);
                Assert.assertEquals(request.getHeader("Authorization"), "token authToken12345");
                Assert.assertEquals(request.getPath(),
                        "/api/repos/" + owner + "/" + repository + "/contents/" + path + "?ref=" + ref);

                Mockito.verify(accessToken).get();
            }
        }
    }

    @Test
    public void loadContentsMimeEncoding() throws Exception {
        String responseJson = null;
        String expectedContents = "productionFiles:\n" +
                "   include:\n" +
                "      - '**/README*'\n" +
                "releaseNoteFiles:\n" +
                "   include:\n" +
                "      - '**/CHANGE*LOG*'\n" +
                "      - '**/RELEASE*NOTES*'\n";

        try (BufferedReader reader = getClasspathReader(
                TEST_RESOURCE_FOLDER.resolve("fileContentResponseMimeEncoding.json"))) {
            responseJson = reader.lines()
                    .collect(Collectors.joining("\n"));
        }

        MockResponse response = new MockResponse()
                .addHeader("Content-Type", MediaTypes.APP_PREVIEW)
                .setBody(responseJson);

        String owner = "owner";
        String repository = "repository";
        String ref = "ref";
        String path = "path.json";

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(response);
            server.start();

            String repositoryUrl = server.url("/api/repos/" + owner + "/" + repository).toString();

            ConfigurationFileLoader contentLoader = new ConfigurationFileLoader("userAgent", path);

            Mockito.when(accessToken.get()).thenReturn("token authToken12345");

            try {
                Optional<String> result = contentLoader.loadContents(accessToken, repositoryUrl, ref);

                Assert.assertNotNull(result);
                Assert.assertTrue(result.isPresent());
                Assert.assertEquals(result.get(), expectedContents);
            } finally {
                Assert.assertEquals(server.getRequestCount(), 1);
                RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

                Assert.assertEquals(request.getHeader("User-Agent"), "userAgent");
                Assert.assertEquals(request.getHeader("Accept"), MediaTypes.APP_PREVIEW);
                Assert.assertEquals(request.getHeader("Authorization"), "token authToken12345");
                Assert.assertEquals(request.getPath(),
                        "/api/repos/" + owner + "/" + repository + "/contents/" + path + "?ref=" + ref);

                Mockito.verify(accessToken).get();
            }
        }
    }

    private BufferedReader getClasspathReader(Path filePath) {
        return new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath.toString()),
                        StandardCharsets.UTF_8));
    }

}
