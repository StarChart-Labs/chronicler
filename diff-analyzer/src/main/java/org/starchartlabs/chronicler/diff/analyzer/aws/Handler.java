/*
 * Copyright (c) Sep 17, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.diff.analyzer.aws;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.chronicler.calamari.core.auth.ApplicationKey;
import org.starchartlabs.chronicler.calamari.core.auth.InstallationAccessToken;
import org.starchartlabs.chronicler.diff.analyzer.AnalysisResults;
import org.starchartlabs.chronicler.diff.analyzer.PullRequestAnalyzer;
import org.starchartlabs.chronicler.events.GitHubPullRequestEvent;
import org.starchartlabs.chronicler.github.model.pullrequest.StatusRequest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

public class Handler implements RequestHandler<SNSEvent, Void> {

    private static final String PARAMETER_STORE_SECRET_KEY = "GITHUB_APP_KEY_SSM";

    private static final String APPLICATION_ID = getAppId();

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Void handleRequest(SNSEvent input, Context context) {
        Collection<GitHubPullRequestEvent> events = getEvents(input);

        if (!events.isEmpty()) {
            logger.trace("Received SNS event: " + input);

            ApplicationKey applicationKey = new ApplicationKey(APPLICATION_ID, Handler::getApplicationKey);

            // TODO romeara make parallel? Will we actually get multiple?
            // TODO romeara Move down to a cloud-provider agnostic library?
            for (GitHubPullRequestEvent event : events) {
                logger.info("Processing pull request for {}", event.getLoggableRepositoryName());

                InstallationAccessToken accessToken = InstallationAccessToken
                        .forRepository(applicationKey.getKeyHeaderSupplier(), event.getBaseRepositoryUrl());
                Supplier<String> accessTokenSupplier = accessToken.getTokenHeaderSupplier();

                // Sset pending status
                StatusRequest pendingRequest = new StatusRequest("pending", "Analysis in progress",
                        "doc/chronicler");
                pendingRequest.sendRequest(event.getPullRequestStatusesUrl(), accessTokenSupplier);

                PullRequestAnalyzer analyzer = new PullRequestAnalyzer(event.getPullRequestUrl(), accessTokenSupplier);

                AnalysisResults results = analyzer.analyze();

                logger.info("Analysis results: prod: {}, rel: {}", results.isModifyingProductionFiles(),
                        results.isModifyingReleaseNotes());

                // Set resolution status
                StatusRequest status = getResult(results);
                status.sendRequest(event.getPullRequestStatusesUrl(), accessTokenSupplier);
            }
        }

        return null;
    }

    private Collection<GitHubPullRequestEvent> getEvents(SNSEvent input) {
        return input.getRecords().stream()
                .map(SNSRecord::getSNS)
                .map(SNS::getMessage)
                .map(GitHubPullRequestEvent::fromJson)
                .collect(Collectors.toSet());
    }

    // TODO romeara this is duplicated - library?
    private static String getApplicationKey() {
        AWSSimpleSystemsManagement systemsManagementClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();

        GetParameterRequest getParameterRequest = new GetParameterRequest();
        getParameterRequest.withName(getParameterStoreSecretKey());
        getParameterRequest.setWithDecryption(true);

        GetParameterResult result = systemsManagementClient.getParameter(getParameterRequest);

        String keyValue = result.getParameter().getValue();

        return "-----BEGIN RSA PRIVATE KEY----- \n" + keyValue + "\n-----END RSA PRIVATE KEY-----";
    }

    private static String getParameterStoreSecretKey() {
        return Objects.requireNonNull(System.getenv(PARAMETER_STORE_SECRET_KEY));
    }

    // TODO romeara Temporary until can figure out how to reference SSM in serverless.yml
    private static String getAppId() {
        AWSSimpleSystemsManagement systemsManagementClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();

        GetParameterRequest getParameterRequest = new GetParameterRequest();
        getParameterRequest.withName(System.getenv("GITHUB_APP_ID_SSM"));
        getParameterRequest.setWithDecryption(false);

        GetParameterResult result = systemsManagementClient.getParameter(getParameterRequest);

        return result.getParameter().getValue();
    }

    // TODO move out from handler
    private static StatusRequest getResult(AnalysisResults results) {
        String state = (results.isDocumented() ? "success" : "failure");
        String description = null;

        if (results.isDocumented()) {
            if (results.isModifyingProductionFiles()) {
                description = "Release notes updated as required";
            } else {
                description = "No production files modified";
            }
        } else {
            description = "Production files modified without release notes";
        }

        return new StatusRequest(state, description, "doc/chronicler");
    }

}
