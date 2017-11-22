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
package org.starchartlabs.chronicler.integration.github.domain.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.starchartlabs.chronicler.integration.github.domain.api.ICredentialManager;
import org.starchartlabs.chronicler.integration.github.domain.impl.CredentialManager;

/**
 * Dependency injection configuration utilizing the Spring Framework to provide domain-level services to the application
 *
 * @author romeara
 * @since 0.1.0
 */
@Configuration
public class GitHubIntegrationDomainConfiguration {

    @Bean
    public ICredentialManager credentialManager(
            @Value("${org.starchartlabs.lockdown.credentials}") String credentialFile,
            @Value("${org.starchartlabs.lockdown.credentials.key}") String privateKey) throws Exception {
        return new CredentialManager(Paths.get(credentialFile), Paths.get(privateKey));
    }

}
