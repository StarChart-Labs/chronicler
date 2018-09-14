/*
 * Copyright (c) Sep 7, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.aws.github.webhook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.util.StringUtils;

//TODO romeara Make a request mapping template and library pairing for standardized handling
// (Or a series of templates and classes which can be built from the Map for handling)
/**
 * Used with API Gateway request template in README
 *
 * @author romeara
 */
public class LambdaRequestHandler implements RequestHandler<Map<String, Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        WebhookVerifier verifier = new WebhookVerifier(this::getWebhookSecret);

        Map<String, Collection<String>> headers = getHeaders(input);
        String jsonRequest = input.get("body-raw-json").toString();

        // TODO Verify conditions of header
        String securityKey = headers.get("X-Hub-Signature").stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No webhook verification header"));

        boolean result = verifier.isPayloadLegitimate(securityKey, jsonRequest);

        String output = "Verification: " + result + ", Headers: " + headers + ", JSON Request: " + jsonRequest;

        // TODO romeara
        if (result) {
            AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                    .withCredentials(new CredentialsProvider())
                    .build();

            PublishRequest publishReq = new PublishRequest()
                    .withTopicArn("arn:aws:sns:us-east-2:895508338844:hackathon-chronicler-github-events")
                    .withMessage("GH: " + output);
            snsClient.publish(publishReq);
        }

        context.getLogger().log(output);

        return output;
    }

    private Map<String, Collection<String>> getHeaders(Map<String, Object> input) {
        Objects.requireNonNull(input);

        Map<String, Collection<String>> result = new HashMap<>();
        Object headersEntry = input.get("headers");

        if (headersEntry instanceof Map) {
            Map<?, ?> headers = (Map<?, ?>) headersEntry;

            for (Entry<?, ?> entry : headers.entrySet()) {
                Collection<String> values = Optional.ofNullable(result.get(entry.getKey().toString()))
                        .orElse(new ArrayList<>());

                values.add(entry.getValue().toString());
                result.put(entry.getKey().toString(), values);
            }
        }

        return result;
    }

    private String getWebhookSecret() {
        return Objects.requireNonNull(System.getenv("GITHUB_WEBHOOK_SECRET"));
    }

    public class CredentialsProvider implements AWSCredentialsProvider {

        @Override
        public AWSCredentials getCredentials() {
            String accessKey = Objects.requireNonNull(System.getenv("SNS_ACCESS_KEY"));
            String secretKey = Objects.requireNonNull(System.getenv("SNS_SECRET"));

            accessKey = StringUtils.trim(accessKey);
            secretKey = StringUtils.trim(secretKey);

            return new BasicAWSCredentials(accessKey, secretKey);
        }

        @Override
        public void refresh() {
        }

    }

}
