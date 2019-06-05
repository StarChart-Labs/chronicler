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
package org.starchartlabs.chronicler.webhook.handler;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.chronicler.events.GitHubPullRequestEvent;
import org.starchartlabs.chronicler.github.model.webhook.InstallationEvent;
import org.starchartlabs.chronicler.github.model.webhook.InstallationRepositoriesEvent;
import org.starchartlabs.chronicler.github.model.webhook.MarketplaceEvent;
import org.starchartlabs.chronicler.github.model.webhook.PingEvent;
import org.starchartlabs.chronicler.github.model.webhook.PullRequestEvent;

/**
 * Handles processing GitHub webhook event payloads
 *
 * @author romeara
 */
public class WebhookEventConverter {

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Consumer<Integer> installationRecorder;

    public WebhookEventConverter(Consumer<Integer> installationRecorder) {
        this.installationRecorder = Objects.requireNonNull(installationRecorder);
    }

    public Optional<GitHubPullRequestEvent> handleEvent(String eventType, String body) {
        Objects.requireNonNull(eventType);
        Objects.requireNonNull(body);

        GitHubPullRequestEvent result = null;

        if (PingEvent.isCompatibleWithEventType(eventType)) {
            PingEvent event = PingEvent.fromJson(body);

            logger.info("GitHub Ping: {}", event.getZen());
        } else if (PullRequestEvent.isCompatibleWithEventType(eventType)) {
            PullRequestEvent event = PullRequestEvent.fromJson(body);

            logger.info("Received pull request event ({}, pr:{})", event.getLoggableRepositoryName(),
                    event.getAction());

            if (event.isCommitChangeType()) {
                result = new GitHubPullRequestEvent(
                        event.getNumber(),
                        event.getLoggableRepositoryName(),
                        event.getPullRequestUrl(),
                        event.getBaseRepositoryUrl(),
                        event.getBaseRef(),
                        event.getPullRequestStatusesUrl(),
                        event.getHeadCommitSha());
            }
        } else if (InstallationEvent.isCompatibleWithEventType(eventType)) {
            InstallationEvent event = InstallationEvent.fromJson(body);

            event.getLoggableRepositoryNames().stream()
            .forEach(repo -> logger.info("GitHub Install: {}: {}", event.getAction(), repo));

            if (event.getLoggableRepositoryNames().isEmpty()) {
                logger.info("GitHub Account Install: {}: {}", event.getAction(), event.getAccountName());
            }

            if (event.isInstallation()) {
                installationRecorder.accept(event.getLoggableRepositoryNames().size());
            }
        } else if (InstallationRepositoriesEvent.isCompatibleWithEventType(eventType)) {
            InstallationRepositoriesEvent event = InstallationRepositoriesEvent.fromJson(body);

            event.getLoggableRepositoryNames().stream()
            .forEach(repo -> logger.info("GitHub Install: {}: {}", event.getAction(), repo));

            if (event.getLoggableRepositoryNames().isEmpty()) {
                logger.info("GitHub Account Install: {}: {}", event.getAction(), event.getAccountName());
            }

            if (event.isInstallation()) {
                installationRecorder.accept(event.getLoggableRepositoryNames().size());
            }
        } else if (MarketplaceEvent.isCompatibleWithEventType(eventType)) {
            MarketplaceEvent event = MarketplaceEvent.fromJson(body);

            logger.info("Marketplace action {} performed by {} ({})", event.getAction(), event.getAccountLogin(),
                    event.getAccountType());
        } else {
            logger.debug("Received unhandled event type: {}", eventType);
        }

        return Optional.ofNullable(result);
    }

}
