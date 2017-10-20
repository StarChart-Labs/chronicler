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
package org.starchartlabs.chronicler.integration.github.webhook;

/**
 * Headers presented by webhook events dispatched from GitHub
 *
 * @author romeara
 * @since 0.1.0
 */
public final class WebhookHeaders {

    public static final String WEBHOOK_EVENT_TYPE = "X-GitHub-Event";

    public static final String WEBHOOK_EVENT_ID = "X-GitHub-Delivery";

    public static final String WEBHOOK_SECURITY = "X-Hub-Signature";

    /**
     * Prevent instantiation of utility class
     */
    private WebhookHeaders() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }

}
