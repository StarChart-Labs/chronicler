/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package org.starchartlabs.chronicler.integration.github;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GitHubWebhookRestServer {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(GitHubWebhookRestServer.class);

    @RequestMapping(path = "/webhook", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<Void> receiveWebhook(@RequestHeader(Headers.WEBHOOK_EVENT_TYPE) String eventType,
            @RequestHeader(Headers.WEBHOOK_EVENT_ID) String gitHubId,
            @RequestHeader(Headers.WEBHOOK_SECURITY) String securityKey,
            HttpServletRequest request) throws IOException {
        logger.debug("Received GitHub Event: {}:{}", gitHubId, eventType);

        String jsonBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);

        // TODO Auto-generated function stub
        throw new UnsupportedOperationException("GitHubWebhookRestServer.receiveWebhook is not yet implemented");
    }

}
