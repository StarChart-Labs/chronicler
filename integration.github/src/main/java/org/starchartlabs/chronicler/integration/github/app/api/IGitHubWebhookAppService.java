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
package org.starchartlabs.chronicler.integration.github.app.api;

import java.io.IOException;

/**
 * Represents handling of GitHub webhook operations available to REST endpoints
 *
 * @author romeara
 * @since 0.1.0
 */
public interface IGitHubWebhookAppService {

    /**
     * Takes an event payload from GitHub and dispatches it to application operations
     *
     * @param securityKey
     *            The verification key provided by GitHub to verify the source of the request
     * @param eventType
     *            The type of event represented by the payload
     * @param payload
     *            The JSON data provided by the webhook event
     * @return True if the payload was recognized and dispatched, false if security verification failed and event was
     *         ignored
     * @throws IOException
     *             If there is an error parsing the JSON payload
     * @since 0.1.0
     */
    boolean acceptPayload(String securityKey, String eventType, String payload) throws IOException;

}
