/*
 * Copyright (c) Sep 21, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.calamari.core;

import java.io.IOException;
import java.io.StringReader;
import java.security.Key;
import java.security.KeyPair;
import java.security.Security;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.starchartlabs.alloy.core.Strings;
import org.starchartlabs.alloy.core.Suppliers;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ApplicationKey {

    // Support necessary security
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // The maximum is 10, allow for some drift
    private static final int EXPIRATION_MINUTES = 9;

    private final String githubApplicationId;

    private final Supplier<String> privateKeySupplier;

    public ApplicationKey(String githubApplicationId, Supplier<String> privateKeySupplier) {
        this.githubApplicationId = Objects.requireNonNull(githubApplicationId);
        this.privateKeySupplier = Objects.requireNonNull(privateKeySupplier);
    }

    public Supplier<String> getKeyHeaderSupplier() {
        return Suppliers.map(getKeySupplier(), ApplicationKey::toAuthorizationHeader);
    }

    // TODO romeara Protected to allow access if needed, use case not certain for non-header value
    protected Supplier<String> getKeySupplier() {
        return Suppliers.memoizeWithExpiration(this::generateNewPayload, EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    private String generateNewPayload() {
        String privateKey = privateKeySupplier.get();

        try (PEMReader r = new PEMReader(new StringReader(privateKey))) {
            KeyPair keyPair = (KeyPair) r.readObject();
            Key key = keyPair.getPrivate();

            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            ZonedDateTime expiration = now.plusMinutes(EXPIRATION_MINUTES);

            JwtBuilder builder = Jwts.builder().setId(null)
                    .setIssuedAt(toDate(now))
                    .setExpiration(toDate(expiration))
                    .setIssuer(githubApplicationId)
                    .signWith(SignatureAlgorithm.RS256, key);

            return builder.compact();
        } catch (IOException e) {
            // TODO romeara - better way to handle this?
            throw new RuntimeException("Error obtaining application authenitcation token", e);
        }
    }

    private static String toAuthorizationHeader(String jwt) {
        Objects.requireNonNull(jwt);

        return Strings.format("Bearer %s", jwt);
    }

    private static Date toDate(ZonedDateTime input) {
        Objects.requireNonNull(input);
        Instant instant = input.toInstant();

        return new Date(instant.toEpochMilli());
    }

}
