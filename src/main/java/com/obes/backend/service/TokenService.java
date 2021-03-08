
package com.obes.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import static com.obes.backend.config.SecurityConstants.TOKEN_PREFIX;
import static com.obes.backend.config.SecurityConstants.EXPIRATION_TIME;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


import java.util.Date;
@Service
public class TokenService {

    @Value("{jwt.SECRET}")
    private String secret;

    public String getUsernameFromToken(String token) {
        String username = JWT.require(Algorithm.HMAC512(secret.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();
        return username;
    }

    public String createToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

}