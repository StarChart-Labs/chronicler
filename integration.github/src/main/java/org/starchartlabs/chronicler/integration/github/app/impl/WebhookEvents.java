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
package org.starchartlabs.chronicler.integration.github.app.impl;

/**
 * Represents header keys used to identify different types of GitHub webhook events
 *
 * <p>
 * Each event has a different defined payload structure associated with it
 *
 * @author romeara
 * @see <a>https://developer.github.com/webhooks/#events</a>
 * @since 0.1.0
 */
public final class WebhookEvents {

    /**
     * A simple ping event to let you know you've set up the webhook correctly
     *
     * @see <a>https://developer.github.com/webhooks/#ping-event</a>
     * @since 0.1.0
     */
    public static final String PING = "ping";

    /**
     * Triggered when a GitHub App has been installed or uninstalled
     *
     * @see <a>https://developer.github.com/v3/activity/events/types/#installationevent</a>
     * @since 0.1.0
     */
    public static final String INSTALLATION = "installation";

    /**
     * Triggered when a repository is added or removed from an installation
     *
     * @see <a>https://developer.github.com/v3/activity/events/types/#installationrepositoriesevent</a>
     * @since 0.1.0
     */
    public static final String INSTALLATION_REPOSITORIES = "installation_repositories";

    /**
     * Triggered when a pull request is assigned, unassigned, labeled, unlabeled, opened, edited, closed, reopened, or
     * synchronized
     *
     * @see <a>https://developer.github.com/v3/activity/events/types/#pullrequestevent</a>
     * @since 0.1.0
     */
    public static final String PULL_REQUEST = "pull_request";

    /**
     * Triggered when a repository is created, made public, or made private
     *
     * @see <a>https://developer.github.com/v3/activity/events/types/#repositoryevent</a>
     * @since 0.1.0
     */
    public static final String REPOSITORY = "repository";


    /**
     * Prevent instantiation of utility class
     */
    private WebhookEvents() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }

}
