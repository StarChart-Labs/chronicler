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
package org.starchartlabs.chronicler.integration.github.domain.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.starchartlabs.chronicler.integration.github.domain.api.ICredentialManager;
import org.starchartlabs.lockdown.CredentialStore;

//TODO romeara doc, test
public class CredentialManager implements ICredentialManager {

    private final CredentialStore credentialStore;

    private final Path privateKey;

    public CredentialManager(Path credentialFile, Path privateKey) throws IOException {
        Objects.requireNonNull(credentialFile);

        this.credentialStore = CredentialStore.load(credentialFile);
        this.privateKey = Objects.requireNonNull(privateKey);
    }

    @Override
    public Supplier<String> getPasswordProvider(String lookupKey) {
        return (() -> readPassword(lookupKey));
    }

    private String readPassword(String lookupKey) {
        Objects.requireNonNull(lookupKey);

        try {
            PasswordReader reader = new PasswordReader();
            credentialStore.accessCredentials(lookupKey, privateKey, reader);

            return reader.getPassword();
        } catch (IOException | InvalidCipherTextException e) {
            throw new IllegalStateException("Invalid private key provided for credential store", e);
        }
    }

    private static final class PasswordReader implements BiConsumer<String, char[]> {

        private char[] password;

        @Override
        public void accept(String username, char[] password) {
            Objects.requireNonNull(password);

            this.password = password;
        }

        private String getPassword() {
            return new String(password);
        }

    }

}
