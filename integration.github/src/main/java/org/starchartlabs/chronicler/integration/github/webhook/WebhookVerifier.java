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