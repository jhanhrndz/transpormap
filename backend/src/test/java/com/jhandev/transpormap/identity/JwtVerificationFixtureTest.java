package com.jhandev.transpormap.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

class JwtVerificationFixtureTest {
    private static final String ISSUER = "https://identity.example.test";
    private static final String AUDIENCE = "transpormap-api";
    private static final Instant NOW = Instant.parse("2026-01-02T12:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static KeyPair trustedKey;
    private static KeyPair wrongKey;

    @BeforeAll
    static void generateSyntheticKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        trustedKey = generator.generateKeyPair();
        wrongKey = generator.generateKeyPair();
    }

    private NimbusJwtDecoder decoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) trustedKey.getPublic()).build();
        OAuth2TokenValidator<Jwt> audienceValidator = jwt ->
                jwt.getAudience() != null && jwt.getAudience().contains(AUDIENCE)
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        OAuth2TokenValidator<Jwt> requiredClaims = jwt ->
                jwt.getExpiresAt() != null && jwt.getSubject() != null && !jwt.getSubject().isBlank()
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        JwtTimestampValidator timestampValidator = new JwtTimestampValidator(Duration.ofSeconds(60));
        timestampValidator.setClock(FIXED_CLOCK);
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                timestampValidator, new JwtIssuerValidator(ISSUER),
                audienceValidator, requiredClaims));
        return decoder;
    }

    private String token(KeyPair key, String issuer, String audience, Instant expiration) throws Exception {
        return token(key, issuer, audience, expiration, "synthetic-user", null);
    }

    private String token(KeyPair key, String issuer, String audience, Instant expiration,
            String subject, Instant notBefore) throws Exception {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder().issueTime(Date.from(NOW.minusSeconds(3600)));
        if (issuer != null) builder.issuer(issuer);
        if (audience != null) builder.audience(audience);
        if (expiration != null) builder.expirationTime(Date.from(expiration));
        if (subject != null) builder.subject(subject);
        if (notBefore != null) builder.notBeforeTime(Date.from(notBefore));
        JWTClaimsSet claims = builder.build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        jwt.sign(new RSASSASigner((RSAPrivateKey) key.getPrivate()));
        return jwt.serialize();
    }

    @Test
    void acceptsValidSignatureIssuerAudienceAndTime() throws Exception {
        String jwt = token(trustedKey, ISSUER, AUDIENCE, NOW.plusSeconds(600));
        assertEquals("synthetic-user", decoder().decode(jwt).getSubject());
    }

    @Test
    void rejectsInvalidSignature() throws Exception {
        String jwt = token(wrongKey, ISSUER, AUDIENCE, NOW.plusSeconds(600));
        assertThrows(JwtException.class, () -> decoder().decode(jwt));
    }

    @Test
    void rejectsUnexpectedIssuer() throws Exception {
        String jwt = token(trustedKey, "https://other.example.test", AUDIENCE, NOW.plusSeconds(600));
        assertThrows(JwtException.class, () -> decoder().decode(jwt));
    }

    @Test
    void rejectsUnexpectedAudience() throws Exception {
        String jwt = token(trustedKey, ISSUER, "other-api", NOW.plusSeconds(600));
        assertThrows(JwtException.class, () -> decoder().decode(jwt));
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        String jwt = token(trustedKey, ISSUER, AUDIENCE, NOW.minusSeconds(600));
        assertThrows(JwtException.class, () -> decoder().decode(jwt));
    }

    @Test
    void rejectsMissingOrBlankRequiredClaims() throws Exception {
        Instant future = NOW.plusSeconds(600);
        String noExpiration = token(trustedKey, ISSUER, AUDIENCE, null);
        String noIssuer = token(trustedKey, null, AUDIENCE, future);
        String noAudience = token(trustedKey, ISSUER, null, future);
        String noSubject = token(trustedKey, ISSUER, AUDIENCE, future, null, null);
        String blankSubject = token(trustedKey, ISSUER, AUDIENCE, future, "  ", null);
        for (String jwt : new String[] {noExpiration, noIssuer, noAudience, noSubject, blankSubject}) {
            assertThrows(JwtException.class, () -> decoder().decode(jwt));
        }
    }

    @Test
    void rejectsTokenNotYetValidOutsideClockSkew() throws Exception {
        String jwt = token(trustedKey, ISSUER, AUDIENCE, NOW.plusSeconds(1200),
                "synthetic-user", NOW.plusSeconds(600));
        assertThrows(JwtException.class, () -> decoder().decode(jwt));
    }

    @Test
    void expirationRespectsSixtySecondFixtureTolerance() throws Exception {
        for (long seconds : new long[] {59, 60}) {
            String jwt = token(trustedKey, ISSUER, AUDIENCE, NOW.minusSeconds(seconds));
            assertEquals("synthetic-user", decoder().decode(jwt).getSubject());
        }
        String expired = token(trustedKey, ISSUER, AUDIENCE, NOW.minusSeconds(61));
        assertThrows(JwtException.class, () -> decoder().decode(expired));
    }

    @Test
    void notBeforeRespectsSixtySecondFixtureTolerance() throws Exception {
        for (long seconds : new long[] {59, 60}) {
            String jwt = token(trustedKey, ISSUER, AUDIENCE, NOW.plusSeconds(600),
                    "synthetic-user", NOW.plusSeconds(seconds));
            assertEquals("synthetic-user", decoder().decode(jwt).getSubject());
        }
        String notYetValid = token(trustedKey, ISSUER, AUDIENCE, NOW.plusSeconds(600),
                "synthetic-user", NOW.plusSeconds(61));
        assertThrows(JwtException.class, () -> decoder().decode(notYetValid));
    }
}
