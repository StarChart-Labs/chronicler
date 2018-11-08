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
import org.starchartlabs.calamari.core.auth.ApplicationKey;
import org.starchartlabs.chronicler.diff.analyzer.PullRequestAnalyzer;
import org.starchartlabs.chronicler.events.GitHubPullRequestEvent;
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
        PullRequestAnalyzer analyzer = new PullRequestAnalyzer(applicationKey);

        getEvents(input).stream()
                .forEach(analyzer::analyze);

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
