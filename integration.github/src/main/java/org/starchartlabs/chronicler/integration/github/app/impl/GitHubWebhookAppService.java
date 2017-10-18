/*
 * Copyright (c) Oct 17, 2017 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.integration.github.app.impl;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.chronicler.integration.github.WebhookVerifier;
import org.starchartlabs.chronicler.integration.github.app.api.IGitHubWebhookAppService;

//TODO romeara doc, test
public class GitHubWebhookAppService implements IGitHubWebhookAppService {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(GitHubWebhookAppService.class);

    // pull_request
    // repository

    private final WebhookVerifier webhookVerifier;

    public GitHubWebhookAppService(WebhookVerifier webhookVerifier) {
        this.webhookVerifier = Objects.requireNonNull(webhookVerifier);
    }

    @Override
    public boolean acceptPayload(String securityKey, String eventType, String payload) {
        boolean validPayload = webhookVerifier.isPayloadLegitimate(securityKey, payload);

        if (validPayload) {
            if (Objects.equals(eventType, "pull_request")) {
                // TODO romeara implement
                logger.info("Received pull request event");
            } else if (Objects.equals(eventType, "repository")) {
                // TODO romeara implement
                logger.info("Received repository event");
            } else {
                logger.warn("Unrecognized event type {}", eventType);
            }
        } else {
            logger.error("Insecure payload delivered!");
        }

        return validPayload;
    }

}
