/*
 * Copyright (c) Sep 26, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.diff.analyzer;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.chronicler.calamari.core.MediaTypes;
import org.starchartlabs.chronicler.calamari.core.PagingLinks;
import org.starchartlabs.chronicler.github.model.Requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//https://developer.github.com/v3/pulls/#list-pull-requests-files
public class PullRequestAnalyzer {

    private static final Gson GSON = new GsonBuilder().create();

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String pullRequestUrl;

    private final Supplier<String> accessTokenSupplier;

    public PullRequestAnalyzer(String pullRequestUrl, Supplier<String> accessTokenSupplier) {
        this.pullRequestUrl = Objects.requireNonNull(pullRequestUrl);
        this.accessTokenSupplier = Objects.requireNonNull(accessTokenSupplier);
    }

    public AnalysisResults analyze() {
        OkHttpClient httpClient = new OkHttpClient();

        AnalysisSettings settings = getSettings();
        boolean productionFiles = false;
        boolean releaseNotes = false;

        HttpUrl url = HttpUrl.get(pullRequestUrl).newBuilder()
                .addPathSegment("files")
                .addQueryParameter("page", "1")
                .addQueryParameter("per_page", "30")
                .build();

        Request.Builder requestBuilder = Requests.newRequest()
                .get()
                .header("Authorization", accessTokenSupplier.get())
                .header("Accept", MediaTypes.APP_PREVIEW)
                .url(url);

        Request request = requestBuilder.build();

        // TODO romeara clean up, duplicates are duplicated....
        try {
            // Iterate until no more files, or both production and release notes found
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                Collection<String> filePaths = getFilePaths(response.body().string());
                PagingLinks pagingLinks = new PagingLinks(response.headers("Link"));

                // TODO Logging debugging
                logger.info("Paths: '{}'", filePaths);

                productionFiles = productionFiles
                        || filePaths.stream().anyMatch(settings::isProductionFile);
                releaseNotes = releaseNotes
                        || filePaths.stream().anyMatch(settings::isReleaseNoteFile);

                while ((!pagingLinks.isLastPage(url.toString()) && pagingLinks.getNextPageUrl().isPresent())
                        && (!productionFiles && !releaseNotes)) {
                    url = HttpUrl.get(pagingLinks.getNextPageUrl().get());
                    request = requestBuilder
                            .header("Authorization", accessTokenSupplier.get())
                            .url(url)
                            .build();

                    response = httpClient.newCall(request).execute();

                    if (response.isSuccessful()) {
                        filePaths = getFilePaths(response.body().string());
                        pagingLinks = new PagingLinks(response.headers("Link"));

                        // TODO Logging debugging
                        logger.info("Paths: '{}'", filePaths);

                        productionFiles = productionFiles
                                || filePaths.stream().anyMatch(settings::isProductionFile);
                        releaseNotes = releaseNotes
                                || filePaths.stream().anyMatch(settings::isReleaseNoteFile);
                    } else {
                        // TODO romeara handling for request limit exceeded logging
                        throw new RuntimeException("Error reading file information (" + response.code() + ")");
                    }
                }

                return new AnalysisResults(productionFiles, releaseNotes);
            } else {
                throw new RuntimeException("Error reading file information (" + response.code() + ")");
            }
        } catch (IOException e) {
            // TODO romeara Better handling?
            throw new RuntimeException(e);
        }
    }

    // TODO externalize to different object to allow reading once per repo, or maybe read from PR'd branch? (maybe
    // warning if setting override in play?)
    private AnalysisSettings getSettings() {
        return AnalysisSettings.builder()
                .includeProduction("**/src/**")
                .excludeProduction("**/test/**")
                .includeReleaseNotes("**/CHANGE*LOG*")
                .includeReleaseNotes("**/RELEASE*NOTES*")
                .build();
    }

    private Collection<String> getFilePaths(String json) {
        // TODO debug loggin
        logger.info("Received JSON: {}", json);
        JsonElement element = GSON.fromJson(json, JsonElement.class);

        return StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(je -> je.get("filename"))
                .map(JsonElement::getAsString)
                .map(path -> path.startsWith("/") ? path : "/" + path)
                .collect(Collectors.toSet());
    }

}
