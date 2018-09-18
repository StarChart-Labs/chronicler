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

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String GITHUB_HASH_HEADER = "X-Hub-Signature";

    private static final String PARAMETER_STORE_SECRET_KEY = "GITHUB_WEBHOOK_SECRET_SSM";

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WebhookVerifier webhookVerifier;

    // Provided for instantiation by AWS Lambda
    public Handler() {
        // TODO actual token lookup
        this(new WebhookVerifier(() -> getKey()));
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
            result = new APIGatewayProxyResponseEvent()
                    .withStatusCode(204);
        }

        return result;
    }

    private static String getParameterStoreSecretKey() {
        return Objects.requireNonNull(System.getenv(PARAMETER_STORE_SECRET_KEY));
    }

    private static String getKey() {
        AWSSimpleSystemsManagement systemsManagementClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();

        GetParameterRequest getParameterRequest = new GetParameterRequest();
        getParameterRequest.withName(getParameterStoreSecretKey());
        getParameterRequest.setWithDecryption(true);

        GetParameterResult result = systemsManagementClient.getParameter(getParameterRequest);

        return result.getParameter().getValue();
    }

}
