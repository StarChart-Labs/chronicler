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
package org.starchartlabs.chronicler.diff.analyzer;

import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.alloy.core.collections.MoreSpliterators;
import org.starchartlabs.alloy.core.collections.PageIterator;
import org.starchartlabs.calamari.core.auth.ApplicationKey;
import org.starchartlabs.calamari.core.auth.InstallationAccessToken;
import org.starchartlabs.calamari.core.paging.GitHubPageIterator;
import org.starchartlabs.chronicler.diff.analyzer.exception.InvalidConfigurationException;
import org.starchartlabs.chronicler.events.GitHubPullRequestEvent;
import org.starchartlabs.chronicler.github.model.Requests;
import org.starchartlabs.chronicler.github.model.pullrequest.StatusHandler;

import com.google.gson.JsonElement;

import okhttp3.HttpUrl;

//https://developer.github.com/v3/pulls/#list-pull-requests-files
public class PullRequestAnalyzer {

    private static final ResourceBundle MESSAGES = ResourceBundle
            .getBundle("org.starchartlabs.chronicler.diff.analyzer.messages");

    private static final String PENDING_MESSAGE_KEY = "status.pending";

    private static final String CONFIGURATION_ERROR_MESSAGE_KEY = "status.error.configuration";

    private static final String PROCESSING_ERROR_MESSAGE_KEY = "status.error.processing";

    private static final String SUCCESS_UPDATED_MESSAGE_KEY = "status.success.updated";

    private static final String SUCCESS_UNNEEDED_MESSAGE_KEY = "status.success.unneeded";

    private static final String FAILURE_MESSAGE_KEY = "status.failure";

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ApplicationKey applicationKey;

    public PullRequestAnalyzer(ApplicationKey applicationKey) {
        this.applicationKey = Objects.requireNonNull(applicationKey);
    }

    public void analyze(GitHubPullRequestEvent event) {
        logger.info("Processing pull request for {}", event.getLoggableRepositoryName());

        InstallationAccessToken accessToken = InstallationAccessToken.forRepository(event.getBaseRepositoryUrl(),
                applicationKey, Requests.USER_AGENT);

        StatusHandler statusHandler = new StatusHandler("doc/chronicler", event.getPullRequestStatusesUrl(),
                accessToken);

        // Set pending status
        statusHandler.sendPending(MESSAGES.getString(PENDING_MESSAGE_KEY));

        // TODO make path a configuration setting
        try {
            AnalysisSettings settings = AnalysisSettings.forRepository(accessToken, event.getBaseRepositoryUrl(),
                    event.getBaseBranch(), ".starchart-labs/chronicler.yml");

            logger.info("Using analysis settings: {}", settings.toString());

            HttpUrl url = HttpUrl.get(event.getPullRequestUrl()).newBuilder()
                    .addPathSegment("files")
                    .addQueryParameter("page", "1")
                    .addQueryParameter("per_page", "30")
                    .build();

            PageIterator<FilePathAnalysis> pageProvider = GitHubPageIterator
                    .gson(url.toString(), accessToken, Requests.USER_AGENT)
                    .map(element -> new FilePathAnalysis(settings, element));

            Spliterator<FilePathAnalysis> spliterator = MoreSpliterators.shortCircuit(
                    MoreSpliterators.ofPaged(pageProvider),
                    (Optional<FilePathAnalysis> a, FilePathAnalysis b) -> b.accumulate(a),
                    FilePathAnalysis::isComplete);

            FilePathAnalysis pathAnalysis = StreamSupport.stream(spliterator, false)
                    .collect(Collectors.reducing(FilePathAnalysis::new))
                    .orElse(new FilePathAnalysis());

            AnalysisResults results = new AnalysisResults(pathAnalysis.containsProductionFiles(),
                    pathAnalysis.containsReleaseNoteFiles());

            logger.info("Analysis results: prod: {}, rel: {}", results.isModifyingProductionFiles(),
                    results.isModifyingReleaseNotes());

            processResult(results, statusHandler);
        } catch (InvalidConfigurationException e) {
            statusHandler.sendError(MESSAGES.getString(CONFIGURATION_ERROR_MESSAGE_KEY));

            throw e;
        } catch (Exception e) {
            statusHandler.sendError(MESSAGES.getString(PROCESSING_ERROR_MESSAGE_KEY));

            throw new RuntimeException("Error processing pull request files", e);
        }
    }

    private void processResult(AnalysisResults results, StatusHandler statusHandler) {
        String description = null;

        if (results.isDocumented()) {
            if (results.isModifyingProductionFiles()) {
                description = MESSAGES.getString(SUCCESS_UPDATED_MESSAGE_KEY);
            } else {
                description = MESSAGES.getString(SUCCESS_UNNEEDED_MESSAGE_KEY);
            }
        } else {
            description = MESSAGES.getString(FAILURE_MESSAGE_KEY);
        }

        if (results.isDocumented()) {
            statusHandler.sendSuccess(description);
        } else {
            statusHandler.sendFailure(description);
        }
    }

    private static final class FilePathAnalysis {

        private final boolean production;

        private final boolean releaseNote;

        public FilePathAnalysis() {
            this(false, false);
        }

        public FilePathAnalysis(AnalysisSettings settings, JsonElement element) {
            Objects.requireNonNull(settings);
            Objects.requireNonNull(element);

            String path = element.getAsJsonObject().get("filename").getAsString();
            path = path.startsWith("/") ? path : "/" + path;

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

        public FilePathAnalysis accumulate(Optional<FilePathAnalysis> accumulated) {
            Objects.requireNonNull(accumulated);

            return accumulated.map(a -> new FilePathAnalysis(a, this)).orElse(this);
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
