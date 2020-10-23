package com.cepheid.cloud.skel.authentication.filters;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.cepheid.cloud.skel.authentication.jwtutils.TokenProvider;
import com.cepheid.cloud.skel.authentication.payload.LoginRequest;
import com.cepheid.cloud.skel.authentication.payload.LoginResponse;
import com.cepheid.cloud.skel.controller.responses.ResponseMessage;
import com.cepheid.cloud.skel.repository.ReaderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public JwtUsernameAndPasswordAuthenticationFilter(
        AuthenticationManager authenticationManager,
        TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper()
                .readValue(request.getInputStream(), LoginRequest.class);

            Authentication authenticate = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), // Principal
                loginRequest.getPassword()); // Credentials

            return authenticationManager.authenticate(authenticate);
        } catch (AuthenticationException | IOException e) {
            // One of the AuthenticationException must be thrown to invoke unsuccessfulAuthentication()
            throw new AuthenticationCredentialsNotFoundException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authResult) throws IOException {

        String token = tokenProvider.generateJwtToken(authResult.getName(), authResult.getAuthorities());
        String authorizationHeader = "Bearer ".concat(token);
        LoginResponse loginResponse = new LoginResponse(authorizationHeader);
        sendResponse(response, OK.value(), loginResponse);
        logger.info("Successful Authentication");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
        throws IOException {
        sendResponse(response, UNAUTHORIZED.value(), new ResponseMessage("Unauthorized!"));
        logger.error("Unsuccessful Authentication!");
    }

    private <T> void sendResponse(HttpServletResponse response, Integer status, T responseBody) throws IOException {
        response.setStatus(status);
        response.setContentType(APPLICATION_JSON.toString());
        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
        response.getOutputStream().flush();
    }
}
