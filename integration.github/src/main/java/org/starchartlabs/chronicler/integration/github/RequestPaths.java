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
package org.starchartlabs.chronicler.integration.github;

/**
 * Contains constants defining request and response paths for the web interface of the application
 *
 * @author romeara
 * @since 0.1.0
 */
public final class RequestPaths {

    /**
     * Path GitHub should POST subscribed events to
     *
     * @since 0.1.0
     */
    public static final String WEBHOOK = "/webhook";

    /**
     * Prevent instantiation of utility class
     */
    private RequestPaths() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }

}
