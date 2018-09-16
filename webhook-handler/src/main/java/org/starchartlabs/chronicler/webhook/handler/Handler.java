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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(Handler.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LOG.info("received: " + input);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(toJsonBody(input));
    }

    private String toJsonBody(Object obj) {
        try {
            // Use "writer()" for thread safety - ObjectWriter is guaranteed thread-safe, object mapper is not
            return OBJECT_MAPPER.writer().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("failed to serialize object", e);
            throw new RuntimeException(e);
        }
    }

}
