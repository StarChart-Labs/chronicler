/*
 * Copyright (c) Sep 14, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.webhook.handler;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.chronicler.calamari.core.WebhookVerifier;
import org.starchartlabs.chronicler.events.GitHubPullRequestEvent;
import org.starchartlabs.chronicler.github.model.webhook.PingEvent;
import org.starchartlabs.chronicler.github.model.webhook.PullRequestEvent;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String GITHUB_HASH_HEADER = "X-Hub-Signature";

    private static final String GITHUB_EVENT_HEADER = "X-GitHub-Event";

    private static final String PARAMETER_STORE_SECRET_KEY = "GITHUB_WEBHOOK_SECRET_SSM";

    private static final String SNS_TOPIC_ARN = System.getenv("SNS_TOPIC_ARN");

    private static final AmazonSNS SNS_CLIENT = AmazonSNSClientBuilder.defaultClient();

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WebhookVerifier webhookVerifier;

    // Provided for instantiation by AWS Lambda
    public Handler() {
        this(new WebhookVerifier(Handler::getKey));
    }

    public Handler(WebhookVerifier webhookVerifier) {
        this.webhookVerifier = Objects.requireNonNull(webhookVerifier);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        logger.trace("Received GitHub Webhook event: " + input);

        APIGatewayProxyResponseEvent result = new APIGatewayProxyResponseEvent()
                .withStatusCode(401);

        String verificationHeader = input.getHeaders().get(GITHUB_HASH_HEADER);
        boolean verified = webhookVerifier.isPayloadLegitimate(verificationHeader, input.getBody());

        if (verified) {
            logger.info("Verified GitHub webhook request");

            result = new APIGatewayProxyResponseEvent()
                    .withStatusCode(204);

            String eventType = input.getHeaders().get(GITHUB_EVENT_HEADER);
            String body = input.getBody();

            if (PingEvent.isCompatibleWithEventType(eventType)) {
                PingEvent event = PingEvent.fromJson(body);

                logger.info("GitHub Ping: {}", event.getZen());
            } else if (PullRequestEvent.isCompatibleWithEventType(eventType)) {
                PullRequestEvent event = PullRequestEvent.fromJson(body);

                logger.info("Received pull request event ({}, pr:{})", event.getLoggableRepositoryName(),
                        event.getAction());

                if (event.isFileChangeType()) {
                    GitHubPullRequestEvent snsEvent = new GitHubPullRequestEvent(
                            event.getNumber(),
                            event.getLoggableRepositoryName(),
                            event.getPullRequestUrl(),
                            event.getBaseRepositoryUrl(),
                            event.getPullRequestStatusesUrl(),
                            event.getHeadCommitSha());

                    PublishRequest publishReq = new PublishRequest()
                            .withTopicArn(SNS_TOPIC_ARN)
                            .withMessage(snsEvent.toJson());
                    SNS_CLIENT.publish(publishReq);
                }
            } else {
                logger.debug("Received unhandled event type: {}", eventType);
            }
        } else {
            logger.warn("Unverified POST received: {}", input);
        }

        return result;
    }

    // TODO romeara this is duplicated - library?
    private static String getKey() {
        AWSSimpleSystemsManagement systemsManagementClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();

        GetParameterRequest getParameterRequest = new GetParameterRequest();
        getParameterRequest.withName(getParameterStoreSecretKey());
        getParameterRequest.setWithDecryption(true);

        GetParameterResult result = systemsManagementClient.getParameter(getParameterRequest);

        return result.getParameter().getValue();
    }

    private static String getParameterStoreSecretKey() {
        return Objects.requireNonNull(System.getenv(PARAMETER_STORE_SECRET_KEY));
    }

}
