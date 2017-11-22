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
package org.starchartlabs.chronicler.integration.github.domain.api;

import java.util.function.Supplier;

/**
 * Provides common management of credential data accessible to the application
 *
 * @author romeara
 * @since 0.1.0
 */
public interface ICredentialManager {

    /**
     * Provides a lookup for a password in the managed credentials
     *
     * @param lookupKey
     *            Key identifying the credentials to read the password from
     * @return A function which may be called to lookup a password when needed
     * @since 0.1.0
     */
    Supplier<String> getPasswordProvider(String lookupKey);

}
