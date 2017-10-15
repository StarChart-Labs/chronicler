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

import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.codec.digest.HmacUtils;

// TODO romeara doc, test
public class WebhookVerifier {

    private final Supplier<String> secureTokenLookup;

    public WebhookVerifier(Supplier<String> secureTokenLookup) {
        this.secureTokenLookup = Objects.requireNonNull(secureTokenLookup);
    }

    public boolean isPayloadLegitimate(@Nullable String securityKey, String payload) {
        Objects.requireNonNull(payload);

        boolean result = false;

        if (securityKey != null) {
            String secureToken = secureTokenLookup.get();
            String expected = "sha1=" + HmacUtils.hmacSha1Hex(secureToken, payload);

            result = Objects.equals(securityKey, expected);
        }

        return result;
    }
}
