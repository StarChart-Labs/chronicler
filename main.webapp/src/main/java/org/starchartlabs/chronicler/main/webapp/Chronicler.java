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
package org.starchartlabs.chronicler.main.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.starchartlabs.chronicler.integration.github.app.config.GitHubIntegrationAppConfiguration;
import org.starchartlabs.chronicler.integration.github.domain.config.GitHubIntegrationDomainConfiguration;
import org.starchartlabs.chronicler.integration.github.server.config.GitHubIntegrationServerConfiguration;
import org.starchartlabs.chronicler.main.webapp.server.config.MainAppServerConfiguration;

/**
 * Entry point for the application. Utilizes the Spring Boot framework to run as a web application, setup dependency
 * injection, and perform other boilerplate tasks
 *
 * @author romeara
 * @since 0.1.0
 */
@SpringBootApplication
@Import({ MainAppServerConfiguration.class,
    GitHubIntegrationServerConfiguration.class,
    GitHubIntegrationAppConfiguration.class,
    GitHubIntegrationDomainConfiguration.class })
public class Chronicler {

    public static void main(String[] args) {
        SpringApplication.run(Chronicler.class, args);
    }

}
