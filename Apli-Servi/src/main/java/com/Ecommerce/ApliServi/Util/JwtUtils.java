package com.Ecommerce.ApliServi.Util;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.*;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.*;

@Component
public class JwtUtils {
    @Value("{security.jwt.key.private}")
    private String Key;
    @Value("{security.jwt.user.generator}")
    private String userG;

    @Value("${security.jwt.token.access-expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.token.refresh-expiration}")
    private long refreshTokenExpiration;

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenExpiration);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenExpiration);
    }

    private String createToken(Authentication authentication, long expirationTime) {
        Algorithm algorithm = Algorithm.HMAC256(Key);
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return JWT.create()
                .withIssuer(userG)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime * 1000L))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(Key);
            return JWT.require(algorithm)
                    .withIssuer(userG)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Token inválido");
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public Claim getClaimFromToken(DecodedJWT token, String claimName) {
        return token.getClaim(claimName);
    }

    public Map<String, Claim> returnClaimsFromToken(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }

}
