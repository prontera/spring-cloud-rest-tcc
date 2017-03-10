package com.github.prontera;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.prontera.common.util.Jacksons;
import org.junit.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * @author Zhao Junjian
 */
public class JwtTester {

    @Test
    public void verify() throws Exception {
        // *******************************   SIGN *****************************************
        String token = JWT.create()
                .withIssuer("solar")
                .withJWTId(UUID.randomUUID().toString())
                // It would not throw an exception even though the token was issued in a past date "iat" < TODAY
                .withIssuedAt(Date.from(Instant.now()))
                // The token hasn't expired yet "exp" > TODAY
                .withExpiresAt(Date.from(OffsetDateTime.now().plusDays(3).toInstant()))
                // The token can already be used. "nbf" > TODAY
                //.withNotBefore(Date.from(OffsetDateTime.now().plusHours(3).toInstant()))
                .withAudience("2")
                .sign(Algorithm.HMAC256("s+gf2lk&$(*@9Nl*]*AS!0sf"));
        System.out.println(token);
        // ********************************   VERIFY **************************************
        // JWTVerifier is reusable
        // If the token has an invalid signature or the Claim requirement is not met, a JWTVerificationException will raise.
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("s+gf2lk&$(*@9Nl*]*AS!0sf"))
                .withIssuer("olar")
                // 5 sec for nbf, iat and exp
                .acceptLeeway(5)
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
        System.out.println(Jacksons.parseInPrettyMode(jwt));
        // ********************************   DECODE **************************************
        JWT.decode(token);
    }

}
