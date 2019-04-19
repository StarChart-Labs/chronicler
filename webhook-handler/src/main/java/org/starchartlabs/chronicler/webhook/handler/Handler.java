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
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.alloy.core.Suppliers;
import org.starchartlabs.calamari.core.webhook.WebhookVerifier;
import org.starchartlabs.chronicler.events.GitHubPullRequestEvent;
import org.starchartlabs.machete.ssm.parameter.SecuredParameter;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    private static final String GITHUB_HASH_HEADER = "X-Hub-Signature";

    private static final String GITHUB_EVENT_HEADER = "X-GitHub-Event";

    private static final String PARAMETER_STORE_SECRET_KEY = "GITHUB_WEBHOOK_SECRET_SSM";

    private static final String SNS_TOPIC_ARN = System.getenv("SNS_TOPIC_ARN");

    private static final String METRIC_NAMESPACE = System.getenv("METRIC_NAMESPACE");;

    private static final AmazonSNS SNS_CLIENT = AmazonSNSClientBuilder.defaultClient();

    private static final AmazonCloudWatch CLOUDWATCH_CLIENT = AmazonCloudWatchClientBuilder.defaultClient();

    private final WebhookVerifier webhookVerifier;

    private final WebhookEventConverter webhookEventConverter;

    // Provided for instantiation by AWS Lambda
    public Handler() {
        this(new WebhookVerifier(getWebhookSecretSupplier()), new WebhookEventConverter(Handler::recordInstallation));
    }

    public Handler(WebhookVerifier webhookVerifier, WebhookEventConverter webhookEventConverter) {
        this.webhookVerifier = Objects.requireNonNull(webhookVerifier);
        this.webhookEventConverter = Objects.requireNonNull(webhookEventConverter);
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

            Optional<GitHubPullRequestEvent> snsEvent = webhookEventConverter.handleEvent(eventType, body);

            snsEvent
            .map(GitHubPullRequestEvent::toJson)
            .map(event -> toSnsRequest(GitHubPullRequestEvent.SUBJECT, event))
            .ifPresent(SNS_CLIENT::publish);
        } else {
            logger.warn("Unverified POST received: {}", input);
        }

        return result;
    }

    private PublishRequest toSnsRequest(String subject, String eventBody) {
        return new PublishRequest()
                .withSubject(subject)
                .withTopicArn(SNS_TOPIC_ARN)
                .withMessage(eventBody);
    }

    private static void recordInstallation(int installations) {
        logger.info("Recording {} installations to AWS namespace {}", installations, METRIC_NAMESPACE);

        Dimension dimension = new Dimension()
                .withName("INSTALLATIONS")
                .withValue("REPOSITORIES");

        MetricDatum datum = new MetricDatum()
                .withMetricName("INSTALLATIONS")
                .withUnit(StandardUnit.Count)
                .withValue(Integer.valueOf(installations).doubleValue())
                .withDimensions(dimension);

        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace(METRIC_NAMESPACE)
                .withMetricData(datum);

        CLOUDWATCH_CLIENT.putMetricData(request);
    }

    private static Supplier<String> getWebhookSecretSupplier() {
        return Suppliers.memoizeWithExpiration(SecuredParameter.fromEnv(PARAMETER_STORE_SECRET_KEY),
                10, TimeUnit.MINUTES);
    }

}
