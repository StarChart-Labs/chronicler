/*
 * Copyright (c) Oct 11, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.test.calamari.core.webhook;

import org.starchartlabs.chronicler.calamari.core.webhook.WebhookVerifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WebhookVerifierTest {

    private static final String SECURE_TOKEN = "12345";

    private static final String PAYLOAD = "{ \"json\": \"json\" }";

    // Generated using an online HMAC-SHA1 tool to reduce chance of bug-masking
    private static final String EXPECTED_HMAC = "24510be1a28ed09a521e5929842ca47ebc05b414";

    private final WebhookVerifier webhookVerifier = new WebhookVerifier(() -> SECURE_TOKEN);

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullSecretLookup() throws Exception {
        new WebhookVerifier(null);
    }

    @Test
    public void isPayloadLegitimateNullSecurityKey() throws Exception {
        boolean result = webhookVerifier.isPayloadLegitimate(null, PAYLOAD);

        Assert.assertFalse(result);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void isPayloadLegitimateNullPayload() throws Exception {
        webhookVerifier.isPayloadLegitimate("sha1=" + EXPECTED_HMAC, null);
    }

    @Test
    public void isPayloadLegitimateUnmatchedSecurityKey() throws Exception {
        boolean result = webhookVerifier.isPayloadLegitimate("sha1=" + EXPECTED_HMAC + "nope", PAYLOAD);

        Assert.assertFalse(result);
    }

    @Test
    public void isPayloadLegitimateUnmatchedPayload() throws Exception {
        boolean result = webhookVerifier.isPayloadLegitimate("sha1=" + EXPECTED_HMAC, PAYLOAD + "{}");

        Assert.assertFalse(result);
    }

    @Test
    public void isPayloadLegitimate() throws Exception {
        boolean result = webhookVerifier.isPayloadLegitimate("sha1=" + EXPECTED_HMAC, PAYLOAD);

        Assert.assertTrue(result);
    }

}
