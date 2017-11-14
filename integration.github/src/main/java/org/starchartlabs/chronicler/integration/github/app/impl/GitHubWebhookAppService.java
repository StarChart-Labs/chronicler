/*
 * Copyright 2017 StarChart Labs Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.starchartlabs.chronicler.integration.github.app.impl;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.starchartlabs.chronicler.integration.github.app.api.IGitHubWebhookAppService;
import org.starchartlabs.chronicler.integration.github.app.model.InstallationEvent;
import org.starchartlabs.chronicler.integration.github.app.model.InstallationRepositoriesEvent;
import org.starchartlabs.chronicler.integration.github.app.model.PingEvent;
import org.starchartlabs.chronicler.integration.github.app.model.PullRequestEvent;
import org.starchartlabs.chronicler.integration.github.app.model.RepositoryEvent;
import org.starchartlabs.chronicler.integration.github.app.model.TargetRepositoryMetaData;
import org.starchartlabs.chronicler.integration.github.domain.model.PullRequestAlteredEvent;
import org.starchartlabs.chronicler.integration.github.domain.model.RepositoryPrivatizedEvent;
import org.starchartlabs.chronicler.integration.github.webhook.WebhookEvents;
import org.starchartlabs.chronicler.integration.github.webhook.WebhookVerifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

/**
 * Implementation of {@link IGitHubWebhookAppService} which dispatches internal application events based on webhook data
 * provided by GitHub
 *
 * <p>
 * Clients should not reference this implementation directly - use dependency injection to obtain the application's
 * selected implementation of {@link IGitHubWebhookAppService}
 *
 * @author romeara
 * @since 0.1.0
 */
public class GitHubWebhookAppService implements IGitHubWebhookAppService {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(GitHubWebhookAppService.class);

    private static final Set<String> PULL_REQUEST_ALTERED_ACTIONS = ImmutableSet.<String> builder()
            .add("opened")
            .add("edited")
            .add("synchronize")
            .add("reopened")
            .build();

    private final WebhookVerifier webhookVerifier;

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * @param webhookVerifier
     *            Represents checks used to verify that payloads come from GitHub and not another party
     * @param applicationEventPublisher
     *            Allows publishing of events consumed by listeners within the application
     * @since 0.1.0
     */
    public GitHubWebhookAppService(WebhookVerifier webhookVerifier,
            ApplicationEventPublisher applicationEventPublisher) {
        this.webhookVerifier = Objects.requireNonNull(webhookVerifier);
        this.applicationEventPublisher = Objects.requireNonNull(applicationEventPublisher);
    }

    @Override
    public boolean acceptPayload(String securityKey, String eventType, String payload) throws IOException {
        Objects.requireNonNull(eventType);
        Objects.requireNonNull(payload);

        boolean validPayload = webhookVerifier.isPayloadLegitimate(securityKey, payload);

        ObjectMapper mapper = new ObjectMapper();
        mapper = mapper.findAndRegisterModules();

        if (validPayload) {
            if (Objects.equals(eventType, WebhookEvents.PING)) {
                PingEvent event = mapper.readValue(payload, PingEvent.class);

                logger.info("Received ping event. GitHub imparts wisdom: {}", event.getZen());
            } else if (Objects.equals(eventType, WebhookEvents.PULL_REQUEST)) {
                PullRequestEvent event = mapper.readValue(payload, PullRequestEvent.class);

                if (PULL_REQUEST_ALTERED_ACTIONS.contains(event.getAction())) {
                    PullRequestAlteredEvent applicationEvent = new PullRequestAlteredEvent(
                            event.getPullRequest().getId(),
                            event.getPullRequest().getNumber(),
                            event.getPullRequest().getUrl(),
                            event.getPullRequest().getStatusesUrl());

                    applicationEventPublisher.publishEvent(applicationEvent);

                    logger.info("Received pull request event ({}: {})", event.getPullRequest().getUrl(),
                            event.getAction());
                } else {
                    logger.debug("Received pull request event ({}: {})", event.getPullRequest().getUrl(),
                            event.getAction());
                }
            } else if (Objects.equals(eventType, WebhookEvents.REPOSITORY)) {
                RepositoryEvent event = mapper.readValue(payload, RepositoryEvent.class);

                if (Objects.equals("privatized", event.getAction())) {
                    RepositoryPrivatizedEvent applicationEvent = new RepositoryPrivatizedEvent(
                            event.getRepository().getId(),
                            event.getRepository().getOwner().getLogin(),
                            event.getRepository().getName());

                    applicationEventPublisher.publishEvent(applicationEvent);

                    logger.info("Received repository event ({}: {})", event.getRepository().getFullName(),
                            event.getAction());
                } else {
                    logger.debug("Received repository event ({}: {})", event.getRepository().getFullName(),
                            event.getAction());
                }
            } else if (Objects.equals(eventType, WebhookEvents.INSTALLATION)) {
                InstallationEvent event = mapper.readValue(payload, InstallationEvent.class);

                logger.info("Received installation event ({}:{})", event.getInstallation().getAccount().getLogin(),
                        event.getAction());
            } else if (Objects.equals(eventType, WebhookEvents.INSTALLATION_REPOSITORIES)) {
                InstallationRepositoriesEvent event = mapper.readValue(payload, InstallationRepositoriesEvent.class);

                String added = event.getRepositoriesAdded().stream()
                        .map(TargetRepositoryMetaData::getFullName)
                        .collect(Collectors.joining(", "));

                String removed = event.getRepositoriesRemoved().stream()
                        .map(TargetRepositoryMetaData::getFullName)
                        .collect(Collectors.joining(", "));

                logger.info("Received installation_repositories event ((): {}), (Added: {}), (Removed: {})",
                        event.getInstallation().getAccount().getLogin(), event.getAction(), added, removed);
            } else {
                logger.warn("Unrecognized event type {}", eventType);
            }
        } else {
            logger.error("Insecure payload delivered!");
        }

        return validPayload;
    }

}
