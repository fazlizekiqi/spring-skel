package com.cepheid.cloud.skel;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.cepheid.cloud.skel.authentication.filters.JwtTokenVerifier;
import com.cepheid.cloud.skel.authentication.filters.JwtUsernameAndPasswordAuthenticationFilter;
import com.cepheid.cloud.skel.authentication.jwtutils.TokenProvider;
import com.cepheid.cloud.skel.exceptions.RestAuthenticationEntryPoint;
import com.google.common.collect.ImmutableList;
import javax.ws.rs.DELETE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;

    @Autowired
    public WebSecurity(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ImmutableList.of("http://localhost:9443", "http://localhost:4200"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers()
            .frameOptions()
            .sameOrigin().and()
            .cors().and()
            .csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(new RestAuthenticationEntryPoint())
            .and()
            .addFilter(jwtUsernameAndPasswordAuthenticationFilter())
            .addFilterAfter(new JwtTokenVerifier(tokenProvider), JwtUsernameAndPasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers(
                "/app/api/reader/**", // LOGIN & SIGNUP
                "/css/*",
                "/h2-console/**",
                "/css/*",
                "/actuator/**",
                "/swagger/**",
                "/js/* ").permitAll()
            .antMatchers(HttpMethod.DELETE,"/app/api/1.0/book/**").hasAnyAuthority("ADMIN")
            .antMatchers(HttpMethod.POST,"/app/api/1.0/book/**").hasAnyAuthority("ADMIN")
            .antMatchers(HttpMethod.PATCH,"/app/api/1.0/book/**").hasAnyAuthority("ADMIN")
            .antMatchers(HttpMethod.GET,"/app/api/1.0/book/**").hasAnyAuthority("ADMIN","USER")
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .anyRequest()
            .authenticated();
    }


    public JwtUsernameAndPasswordAuthenticationFilter jwtUsernameAndPasswordAuthenticationFilter() throws Exception {
        JwtUsernameAndPasswordAuthenticationFilter jwt = new JwtUsernameAndPasswordAuthenticationFilter(
            authenticationManager(), tokenProvider);
        jwt.setFilterProcessesUrl("/app/api/reader/login");
        return jwt;
    }


}
