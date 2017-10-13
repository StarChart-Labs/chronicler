package org.starchartlabs.chronicler.integration.github;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyPair;
import java.security.Security;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

// TODO romeara doc, test
// Apply directly to rest template: new RestTemplateBuilder().additionalInterceptors(obj).build()
public class ApplicationKeyProvider implements Supplier<HttpHeaders> {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(ApplicationKeyProvider.class);

    private static final String GITHUB_APP_JWT_KEY = "GITHUB-APP";

    private final String gitHubPrivateKeyPath;

    private final String gitHubApplicationId;

    private final Cache<String, String> payloads;

    public ApplicationKeyProvider(String gitHubPrivateKeyPath, String gitHubApplicationId) {
        this.gitHubPrivateKeyPath = Objects.requireNonNull(gitHubPrivateKeyPath);
        this.gitHubApplicationId = Objects.requireNonNull(gitHubApplicationId);

        payloads = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(8, TimeUnit.MINUTES)
                .build();

        // Allow use of bouncy castle key provider
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public HttpHeaders get() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getJwtPayload());

        return headers;
    }

    private String getJwtPayload() {
        try {
            return payloads.get(GITHUB_APP_JWT_KEY, this::generateNewPayload);
        } catch (ExecutionException e) {
            logger.error("Error generating GitHub App JWT payload", e);

            Throwables.throwIfUnchecked(e.getCause());
            throw new RuntimeException(e.getCause());
        }
    }

    private String generateNewPayload() throws IOException {
        // TODO romeara allow this to handle classpath or regular file locations
        try (PEMReader r = new PEMReader(new InputStreamReader(new ClassPathResource(gitHubPrivateKeyPath).getInputStream()))) {
            KeyPair keyPair = (KeyPair) r.readObject();
            Key key = keyPair.getPrivate();

            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            ZonedDateTime expiration = now.plusMinutes(9);

            JwtBuilder builder = Jwts.builder().setId(null)
                    .setIssuedAt(toDate(now))
                    .setExpiration(toDate(expiration))
                    .setIssuer(gitHubApplicationId)
                    .signWith(SignatureAlgorithm.RS256, key);

            return builder.compact();
        }
    }

    private static Date toDate(ZonedDateTime input) {
        Objects.requireNonNull(input);
        Instant instant = input.toInstant();

        return new Date(instant.toEpochMilli());
    }

}
