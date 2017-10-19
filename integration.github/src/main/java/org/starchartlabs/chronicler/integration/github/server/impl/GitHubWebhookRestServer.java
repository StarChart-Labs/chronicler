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
package org.starchartlabs.chronicler.integration.github.server.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.starchartlabs.chronicler.integration.github.ContentTypes;
import org.starchartlabs.chronicler.integration.github.Headers;
import org.starchartlabs.chronicler.integration.github.RequestPaths;
import org.starchartlabs.chronicler.integration.github.app.api.IGitHubWebhookAppService;

// TODO romeara test
/**
 * REST server which defines endpoints related receiving webhook events from GitHub
 *
 * @author romeara
 * @since 0.1.0
 */
@Controller
public class GitHubWebhookRestServer {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(GitHubWebhookRestServer.class);

    private final IGitHubWebhookAppService gitHubWebhookAppService;

    /**
     * @param gitHubWebhookAppService
     *            Service which handles application handling of webhook events
     * @since 0.1.0
     */
    public GitHubWebhookRestServer(IGitHubWebhookAppService gitHubWebhookAppService) {
        this.gitHubWebhookAppService = Objects.requireNonNull(gitHubWebhookAppService);
    }

    /**
     * Responses:
     * <ul>
     * <li>204 NO CONTENT - If the event was accepted and dispatched</li>
     * <li>412 PRECONDITION FAILED - If security checks failed and the event was rejected</li>
     * </ul>
     *
     * @param eventType
     *            Header which indicates the structure of the JSON request body
     * @param gitHubId
     *            Header which is the GitHub ID of the event being dispatched
     * @param securityKey
     *            Header which contains a security verification code
     * @param request
     *            The HTTP request made by GitHub
     * @return Representation of the result of the event
     * @throws IOException
     *             If there is an error reading the request body
     * @since 0.1.0
     */
    @RequestMapping(path = RequestPaths.WEBHOOK, method = RequestMethod.POST, consumes = ContentTypes.JSON)
    public ResponseEntity<Void> receiveWebhook(@RequestHeader(Headers.WEBHOOK_EVENT_TYPE) String eventType,
            @RequestHeader(Headers.WEBHOOK_EVENT_ID) String gitHubId,
            @RequestHeader(Headers.WEBHOOK_SECURITY) String securityKey,
            HttpServletRequest request) throws IOException {
        logger.debug("Received GitHub Event: {}:{}", gitHubId, eventType);

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        String jsonBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);

        boolean validPayload = gitHubWebhookAppService.acceptPayload(securityKey, eventType, jsonBody);

        if (validPayload) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return response;
    }

}
