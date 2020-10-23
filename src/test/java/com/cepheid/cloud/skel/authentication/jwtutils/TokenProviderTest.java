package com.cepheid.cloud.skel.authentication.jwtutils;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.authority.AuthorityUtils;

public class TokenProviderTest {

    TokenProvider tokenProvider;
    String username;
    String role;

    @Before
    public void setUp() {
        String secretKey = "securesecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecure";
        SecretKey secretKey1 = Keys.hmacShaKeyFor(secretKey.getBytes());
        tokenProvider = new TokenProvider(secretKey1);
        username = "username";
        role = "USER";
    }

    @Test
    public void token_provider_should_generate_token() {
        String jwtToken = tokenProvider.generateJwtToken(
            username,
            AuthorityUtils.createAuthorityList(role));
        assertThat(jwtToken).isNotNull();
    }

    @Test
    public void token_provider_should_decode_token() {
        String jwtToken = tokenProvider
            .generateJwtToken(username, AuthorityUtils.createAuthorityList(role));
        var claimsJws = tokenProvider.decodeToken(jwtToken);
        var body = claimsJws.getBody();
        var authorities = (List<Map<String, String>>) body.get("authorities");
        var mapAuthorities = authorities.get(0);
        var usernameFromTokenDecoding = body.getSubject();
        String authorityFromTokenDecoding = mapAuthorities.get("authority");
        Date issuedAt = body.getIssuedAt();
        Date expiration = body.getExpiration();
        assertThat(username).isEqualTo(usernameFromTokenDecoding);
        assertThat(role).isEqualTo(authorityFromTokenDecoding);
        assertThat(expiration).isInTheFuture();
        assertThat(expiration).isAfter(issuedAt);
    }

    @Test
    public void token_provider_should_not_decode_token() {
        String token = "Invalid token";
        var claimsJws = tokenProvider.decodeToken(token);
        assertThat(claimsJws).isNull();
    }

    @Test
    public void token_provider_should_get_authorities_from_token() {
        var authority = List.of(Map.of("authority", role));
        var simpleGrantedAuthorities = tokenProvider.getAuthority(authority);
        assertThat(simpleGrantedAuthorities).hasSize(1);
        assertThat(simpleGrantedAuthorities).isNotNull();
    }

    @Test
    public void token_provider_should_remove_bearer_from_token() {
        String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYXpsaXpla2l";
        String tokenWithoutBearer = tokenProvider.removeBearerFromToken(token);
        assertThat(tokenWithoutBearer).doesNotContain("Bearer ");
        assertThat(tokenWithoutBearer).isNotNull();
    }

    @Test
    public void token_provider_should_check_if_string_is_null_or_empty() {
        var some_string = Map.of(
            "some string", false,
            "", true);
        some_string.forEach((string, bool) -> {
            boolean isNullOrEmpty = tokenProvider.isNullOrEmpty(string);
            assertThat(isNullOrEmpty).isEqualTo(bool);
        });
    }
}
