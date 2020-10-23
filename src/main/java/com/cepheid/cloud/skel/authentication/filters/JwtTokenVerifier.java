package com.cepheid.cloud.skel.authentication.filters;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.cepheid.cloud.skel.authentication.jwtutils.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


public class JwtTokenVerifier extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    public JwtTokenVerifier(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        try {
            if (tokenProvider.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = tokenProvider.removeBearerFromToken(authorizationHeader);
            Jws<Claims> claimsJws = tokenProvider.decodeToken(token);
            var jwsBody = claimsJws.getBody();
            var subject = jwsBody.getSubject(); // The Actual username that we pass to subject variable
            var authorities = (List<Map<String, String>>) jwsBody.get("authorities");
            var simpleGrantedAuthorities = tokenProvider.getAuthority(authorities);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                subject,
                null,
                simpleGrantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            response.sendError(FORBIDDEN.value());
            return;
        }
        // After first filter and second filter we want to send back the expected resource back to client

        filterChain.doFilter(request, response);


    }


}
