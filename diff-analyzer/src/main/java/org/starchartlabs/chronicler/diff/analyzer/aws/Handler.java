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
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.alloy.core.Suppliers;
import org.starchartlabs.chronicler.calamari.core.auth.ApplicationKey;
import org.starchartlabs.chronicler.calamari.core.auth.InstallationAccessToken;
import org.starchartlabs.chronicler.diff.analyzer.AnalysisResults;
import org.starchartlabs.chronicler.diff.analyzer.PullRequestAnalyzer;
import org.starchartlabs.chronicler.events.GitHubPullRequestEvent;
import org.starchartlabs.chronicler.github.model.Requests;
import org.starchartlabs.chronicler.github.model.pullrequest.StatusHandler;
import org.starchartlabs.chronicler.machete.SecuredRsaKeyParameter;

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
        logger.trace("Received SNS event: " + input);
        ApplicationKey applicationKey = new ApplicationKey(APPLICATION_ID, getPrivateKeySupplier());

        getEvents(input).stream()
        .forEach(event -> handleEvent(event, applicationKey));

        return null;
    }

    private Collection<GitHubPullRequestEvent> getEvents(SNSEvent input) {
        return input.getRecords().stream()
                .map(SNSRecord::getSNS)
                .filter(sns -> Objects.equals(sns.getSubject(), GitHubPullRequestEvent.SUBJECT))
                .map(SNS::getMessage)
                .map(GitHubPullRequestEvent::fromJson)
                .collect(Collectors.toSet());
    }

    private Supplier<String> getPrivateKeySupplier() {
        return Suppliers.memoizeWithExpiration(SecuredRsaKeyParameter.fromEnv(PARAMETER_STORE_SECRET_KEY),
                10, TimeUnit.MINUTES);
    }

    // TODO move out from handler
    private void handleEvent(GitHubPullRequestEvent event, ApplicationKey applicationKey) {
        logger.info("Processing pull request for {}", event.getLoggableRepositoryName());

        InstallationAccessToken accessToken = InstallationAccessToken.forRepository(event.getBaseRepositoryUrl(),
                applicationKey, Requests.USER_AGENT);

        StatusHandler statusHandler = new StatusHandler("doc/chronicler", event.getPullRequestStatusesUrl(),
                accessToken);

        // Set pending status
        statusHandler.sendPending("Analysis in progress");

        try {
            PullRequestAnalyzer analyzer = new PullRequestAnalyzer(event.getPullRequestUrl(), accessToken);

            AnalysisResults results = analyzer.analyze();

            logger.info("Analysis results: prod: {}, rel: {}", results.isModifyingProductionFiles(),
                    results.isModifyingReleaseNotes());

            // Set resolution status
            processResult(results, statusHandler);
        } catch (Exception e) {
            statusHandler.sendError("Error processing pull request files");

            throw new RuntimeException("Error processing analysis results", e);
        }


    }

    // TODO move out from handler
    private void processResult(AnalysisResults results, StatusHandler statusHandler) {
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

        if (results.isDocumented()) {
            statusHandler.sendSuccess(description);
        } else {
            statusHandler.sendFailure(description);
        }
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

}
