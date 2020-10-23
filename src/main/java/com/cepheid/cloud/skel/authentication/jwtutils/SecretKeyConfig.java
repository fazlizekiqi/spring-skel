package com.cepheid.cloud.skel.authentication.jwtutils;


import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SecretKeyConfig {

    @Value("${app.secretKey}")
    private String secretKey;
    @Bean
    public SecretKey getSecretKeyForSigning() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
