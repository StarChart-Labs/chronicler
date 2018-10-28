/*
 * Copyright (c) Oct 10, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.test.calamari.core.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bouncycastle.openssl.PEMReader;
import org.starchartlabs.chronicler.calamari.core.auth.ApplicationKey;
import org.starchartlabs.chronicler.calamari.core.exception.KeyLoadingException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class ApplicationKeyTest {

    private static final Path TEST_RESOURCE_FOLDER = Paths.get("org", "starchartlabs", "chronicler", "test", "calamari",
            "core", "auth");

    private String privateKey;

    private String invalidKey;

    @BeforeClass
    public void readKeys() {
        privateKey = readPrivateKey();
        invalidKey = readInvalidPrivateKey();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullGitHubAppId() throws Exception {
        new ApplicationKey(null, () -> "string");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullPrivateKeySupplier() throws Exception {
        new ApplicationKey("gitHubAppId", null);
    }

    @Test(expectedExceptions = KeyLoadingException.class)
    public void getInvalidPrivateKey() throws Exception {
        ApplicationKey key = new ApplicationKey("gitHubAppId", () -> invalidKey);

        key.get();
    }

    @Test(expectedExceptions = KeyLoadingException.class)
    public void getUnparsablePrivateKey() throws Exception {
        ApplicationKey key = new ApplicationKey("gitHubAppId", () -> "notAPem");

        key.get();
    }

    @Test
    public void get() throws Exception {
        ApplicationKey key = new ApplicationKey("gitHubAppId", () -> privateKey);

        String result = key.get();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("Bearer "));

        String jwt = result.substring("Bearer ".length());

        try (PEMReader r = new PEMReader(new StringReader(privateKey))) {
            KeyPair keyPair = (KeyPair) r.readObject();
            Key publicKey = keyPair.getPublic();

            Jws<Claims> generatedKey = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(jwt);

            Claims claims = generatedKey.getBody();

            Assert.assertEquals(claims.getIssuer(), "gitHubAppId");
            Assert.assertNotNull(claims.getIssuedAt());
            Assert.assertNotNull(claims.getExpiration());
            Assert.assertTrue(claims.getIssuedAt().before(claims.getExpiration()));
        }
    }

    @Test
    public void getCached() throws Exception {
        CountingSupplier<String> privateKeySupplier = new CountingSupplier<>(() -> privateKey);
        ApplicationKey key = new ApplicationKey("gitHubAppId", privateKeySupplier);

        for (int i = 0; i < 10; i++) {
            Assert.assertNotNull(key.get());
        }

        // The cache time is 9-10 minutes - getting 10 keys should only require reading the private key once, for the
        // first cached call
        Assert.assertEquals(privateKeySupplier.getCount(), 1);
    }

    private String readPrivateKey() {
        // Note: The test key was generated from a GitHub App, and immediately removed as a valid key, and so is not a
        // security issue
        try (BufferedReader reader = getClasspathReader(
                TEST_RESOURCE_FOLDER.resolve("orphaned-github-private-key.pem"))) {
            String key = reader.lines()
                    .collect(Collectors.joining("\n"));
            System.out.println(key);
            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readInvalidPrivateKey() {
        // Note: The test key was generated from a GitHub App, and immediately removed as a valid key, and so is not a
        // security issue
        try (BufferedReader reader = getClasspathReader(
                TEST_RESOURCE_FOLDER.resolve("invalid-github-private-key.pem"))) {
            String key = reader.lines()
                    .collect(Collectors.joining("\n"));
            System.out.println(key);
            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader getClasspathReader(Path filePath) {
        return new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath.toString()),
                        StandardCharsets.UTF_8));
    }

    private static class CountingSupplier<T> implements Supplier<T> {

        private final Supplier<T> delegate;

        private int count;

        public CountingSupplier(Supplier<T> delegate) {
            this.delegate = Objects.requireNonNull(delegate);
            count = 0;
        }

        @Override
        public T get() {
            count++;
            return delegate.get();
        }

        public int getCount() {
            return count;
        }

    }

}
