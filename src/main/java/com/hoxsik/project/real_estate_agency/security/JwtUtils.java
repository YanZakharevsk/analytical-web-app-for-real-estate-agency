package com.hoxsik.project.real_estate_agency.security;

import com.hoxsik.project.real_estate_agency.exceptions.AuthenticationException;
import org.apache.hadoop.shaded.com.nimbusds.jose.*;
import org.apache.hadoop.shaded.com.nimbusds.jose.crypto.MACSigner;
import org.apache.hadoop.shaded.com.nimbusds.jose.crypto.MACVerifier;
import org.apache.hadoop.shaded.com.nimbusds.jwt.JWTClaimsSet;
import org.apache.hadoop.shaded.com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    private static final byte[] SECRET = generateSecret();

    public String extractUsername(String token) {
        return (String) extractAllClaims(token).getClaim("sub");
    }

    public LocalDateTime extractExpirationDate(String token) {
        return (LocalDateTime) extractAllClaims(token).getClaim("exp");
    }

    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) && extractExpirationDate(token).isAfter(LocalDateTime.now());
    }

    public String generateToken(String username) {
        try {
            JWSSigner jwsSigner = new MACSigner(SECRET);
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

            SignedJWT signedJWT = new SignedJWT(jwsHeader, createToken(username));
            signedJWT.sign(jwsSigner);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new AuthenticationException("Failed to generate token");
        }
    }


    private JWTClaimsSet createToken(String username) {
        return new JWTClaimsSet.Builder()
                .subject(username)
                .expirationTime(new Date(new Date().getTime() + TimeUnit.HOURS.toMillis(24)))
                .issueTime(new Date())
                .build();
    }

    private JWTClaimsSet extractAllClaims(String token) {

        try {
            JWSVerifier jwsVerifier = new MACVerifier(SECRET);
            JWSObject jwsObject = JWSObject.parse(token);

            if (!jwsObject.verify(jwsVerifier)) {
                throw new AuthenticationException("Invalid token");
            }

            return JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

        } catch (ParseException | JOSEException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }

    private static byte[] generateSecret() {
        byte[] secret = new byte[32];
        new SecureRandom().nextBytes(secret);
        return secret;
    }
}
