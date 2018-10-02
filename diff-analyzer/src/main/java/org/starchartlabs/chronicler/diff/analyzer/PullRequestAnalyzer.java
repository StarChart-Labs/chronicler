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
import java.util.Objects;
import java.util.function.Supplier;

import org.starchartlabs.chronicler.github.model.PageHandler;

import com.google.gson.JsonElement;

import okhttp3.HttpUrl;

//https://developer.github.com/v3/pulls/#list-pull-requests-files
public class PullRequestAnalyzer {

    private final Supplier<String> accessTokenSupplier;

    public PullRequestAnalyzer(Supplier<String> accessTokenSupplier) {
        this.accessTokenSupplier = Objects.requireNonNull(accessTokenSupplier);
    }

    public AnalysisResults analyze(String pullRequestUrl) {
        AnalysisSettings settings = AnalysisSettings.defaultSettings();

        HttpUrl url = HttpUrl.get(pullRequestUrl).newBuilder()
                .addPathSegment("files")
                .addQueryParameter("page", "1")
                .addQueryParameter("per_page", "30")
                .build();

        try {
            FilePathAnalysis pathAnalysis = new PageHandler(accessTokenSupplier)
                    .pageUntil(url.toString(), o -> toPathAnalysis(o, settings), FilePathAnalysis::isComplete,
                            FilePathAnalysis::new)
                    .orElse(new FilePathAnalysis());

            return new AnalysisResults(pathAnalysis.containsProductionFiles(), pathAnalysis.containsReleaseNoteFiles());
        } catch (IOException e) {
            // TODO romeara Better handling?
            throw new RuntimeException(e);
        }
    }

    private FilePathAnalysis toPathAnalysis(JsonElement element, AnalysisSettings settings) {
        String path = element.getAsJsonObject().get("filename").getAsString();
        path = path.startsWith("/") ? path : "/" + path;

        return new FilePathAnalysis(settings, path);
    }

    private static final class FilePathAnalysis {

        private final boolean production;

        private final boolean releaseNote;

        public FilePathAnalysis() {
            this(false, false);
        }

        public FilePathAnalysis(AnalysisSettings settings, String path) {
            Objects.requireNonNull(settings);
            Objects.requireNonNull(path);

            this.production = settings.isProductionFile(path);
            this.releaseNote = settings.isReleaseNoteFile(path);
        }

        public FilePathAnalysis(FilePathAnalysis a, FilePathAnalysis b) {
            this(a.containsProductionFiles() || b.containsProductionFiles(),
                    a.containsReleaseNoteFiles() || b.containsReleaseNoteFiles());
        }

        private FilePathAnalysis(boolean production, boolean releaseNote) {
            this.production = production;
            this.releaseNote = releaseNote;
        }

        public boolean containsProductionFiles() {
            return production;
        }

        public boolean containsReleaseNoteFiles() {
            return releaseNote;
        }

        // Complete because the end-state of "success" is now guaranteed
        public boolean isComplete() {
            return containsProductionFiles() && containsReleaseNoteFiles();
        }

    }

}
