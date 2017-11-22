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
package org.starchartlabs.chronicler.integration.github.app.config;

import java.util.function.Supplier;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.starchartlabs.chronicler.integration.github.app.api.IGitHubWebhookAppService;
import org.starchartlabs.chronicler.integration.github.app.impl.GitHubWebhookAppService;
import org.starchartlabs.chronicler.integration.github.app.impl.WebhookVerifier;
import org.starchartlabs.chronicler.integration.github.domain.api.ICredentialManager;

/**
 * Dependency injection configuration utilizing the Spring Framework to provide application service-level services to
 * the application
 *
 * @author romeara
 * @since 0.1.0
 */
@Configuration
public class GitHubIntegrationAppConfiguration {

    /**
     * Lookup key which allows lookup of webhook secret information
     */
    private static final String GITHUB_WEBHOOK_SECRET_LOOKUP_KEY = "GITHUB-WEBHOOK";

    @Bean
    public WebhookVerifier webhookVerifier(ICredentialManager credentialManager){
        Supplier<String> secretProvider = credentialManager.getPasswordProvider(GITHUB_WEBHOOK_SECRET_LOOKUP_KEY);

        return new WebhookVerifier(secretProvider);
    }

    @Bean
    public IGitHubWebhookAppService gitHubWebhookAppService(WebhookVerifier webhookVerifier,
            ApplicationEventPublisher applicationEventPublisher) {
        return new GitHubWebhookAppService(webhookVerifier, applicationEventPublisher);
    }

}
