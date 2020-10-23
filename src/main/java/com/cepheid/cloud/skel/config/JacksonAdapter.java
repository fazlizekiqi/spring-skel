package com.cepheid.cloud.skel.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JacksonAdapter implements WebMvcConfigurer {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder()
            .failOnUnknownProperties(false)
            .serializationInclusion(Include.NON_EMPTY)
            .serializerByType(Page.class, new JsonPageSerializer());
    }
}

