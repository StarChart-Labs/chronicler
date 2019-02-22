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

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.alloy.core.Suppliers;
import org.starchartlabs.calamari.core.auth.ApplicationKey;
import org.starchartlabs.chronicler.diff.analyzer.PullRequestAnalyzer;
import org.starchartlabs.chronicler.events.GitHubPullRequestEvent;
import org.starchartlabs.machete.sns.SnsEvents;
import org.starchartlabs.machete.ssm.parameter.SecuredRsaKeyParameter;
import org.starchartlabs.machete.ssm.parameter.StringParameter;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class Handler implements RequestHandler<SNSEvent, Void> {

    private static final String PARAMETER_STORE_SECRET_KEY = "GITHUB_APP_KEY_SSM";

    private static final String PARAMETER_STORE_APP_ID = "GITHUB_APP_ID_SSM";

    private static final Supplier<String> APPLICATION_KEY_SUPPLIER = Suppliers.memoizeWithExpiration(
            SecuredRsaKeyParameter.fromEnv(PARAMETER_STORE_SECRET_KEY),
            10, TimeUnit.MINUTES);

    private static final Supplier<String> APPLICATION_ID_SUPPLIER = Suppliers
            .memoizeWithExpiration(StringParameter.fromEnv(PARAMETER_STORE_APP_ID), 10, TimeUnit.MINUTES);

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Void handleRequest(SNSEvent input, Context context) {
        logger.trace("Received SNS event: " + input);

        ApplicationKey applicationKey = new ApplicationKey(APPLICATION_ID_SUPPLIER.get(), APPLICATION_KEY_SUPPLIER);
        PullRequestAnalyzer analyzer = new PullRequestAnalyzer(applicationKey);

        SnsEvents.getMessages(input, GitHubPullRequestEvent::fromJson, GitHubPullRequestEvent.SUBJECT).stream()
        .forEach(analyzer::analyze);

        return null;
    }

}
