package com.cepheid.cloud.skel.authentication.jwtutils;

import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


@Component
public class TokenProvider {

    public static final Logger log = Logger.getLogger(TokenProvider.class.getName());
    private final SecretKey secretKey;
    @Autowired
    public TokenProvider(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String generateJwtToken(String username, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder()
            .setSubject(username)
            .claim("authorities", authorities)
            .setIssuedAt(new java.util.Date())
            .setExpiration(Date.valueOf(LocalDate.now().plusDays(5)))
            .signWith(secretKey)
            .compact();
    }

    public Jws<Claims> decodeToken(String token) {
        try {
            return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.info("JTW TOKEN EXPIRED");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported Token");
        } catch (MalformedJwtException e) {
            log.info("MalFormed JWT");
        } catch (SignatureException e) {
            log.info("Signature Exception");
        } catch (IllegalArgumentException e) {
            log.info("Illegal Argument");
        }
        return null;
    }

    public Set<SimpleGrantedAuthority> getAuthority(List<Map<String, String>> authorities) {
        return authorities.stream()
            .map(auth -> new SimpleGrantedAuthority(auth.get("authority")))
            .collect(Collectors.toSet());
    }

    public String removeBearerFromToken(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
    }

    public boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

}
