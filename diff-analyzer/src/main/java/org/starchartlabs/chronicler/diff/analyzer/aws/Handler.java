/*
 * Copyright 2019 StarChart-Labs Contributors (https://github.com/StarChart-Labs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
