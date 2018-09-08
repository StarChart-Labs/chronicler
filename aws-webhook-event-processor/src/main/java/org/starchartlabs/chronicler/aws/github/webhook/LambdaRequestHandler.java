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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used with API Gateway request template in README
 * 
 * @author romeara
 *
 */
public class LambdaRequestHandler implements RequestHandler<Map<String, Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        // HttpRequest request = fromMapping(input);

        // return "Hello POST: " + request.getHeaders() + "\n" + request.getBody();

        AwsRequestInput request = new AwsRequestInput(input);

        return "Hello POST: " + request.getSub("params").get("header") + "\n" + request.get("body-json");

        // return input.toString();
    }

    private static class AwsRequestInput {

        private final Map<String, Object> input;

        public AwsRequestInput(Map<String, Object> input) {
            this.input = Objects.requireNonNull(input);
        }

        public String get(String key) {
            return Optional.ofNullable(input.get(key))
                    .map(Object::toString)
                    .orElse(null);
        }

        public AwsRequestInput getSub(String key) {
            return Optional.ofNullable(input.get(key))
                    .map(input -> (Map<String, Object>) input)
                    .map(AwsRequestInput::new)
                    .orElse(new AwsRequestInput(Collections.emptyMap()));
        }

    }

    private static HttpRequest fromMapping(Map<String, Object> input) {
        String body = Optional.ofNullable(input.get("body"))
                .map(Object::toString)
                .orElse("");
        String headerString = Optional.ofNullable(input.get("headers"))
                .map(Object::toString)
                .orElse("");

        Map<String, Collection<String>> headers = new HashMap<>();

        try {
            JsonNode node = new ObjectMapper().reader().readTree(headerString);
            Iterator<Entry<String, JsonNode>> fields = node.fields();

            while (fields.hasNext()) {
                Entry<String, JsonNode> field = fields.next();

                Collection<String> values = Optional.ofNullable(headers.get(field.getKey()))
                        .orElse(new ArrayList<>());
                values.add(field.getValue().toString());

                headers.put(field.getKey(), values);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new HttpRequest(headers, body);
    }

    private static class HttpRequest {

        private final Map<String, Collection<String>> headers;

        private final String body;

        public HttpRequest(Map<String, Collection<String>> headers, String body) {
            this.headers = Objects.requireNonNull(headers);
            this.body = Objects.requireNonNull(body);
        }

        public Map<String, Collection<String>> getHeaders() {
            return headers;
        }

        public String getBody() {
            return body;
        }

    }

}
