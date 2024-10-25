package org.account.mgmtsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    public static final String ZIPPOPOTAM_REST_TEMPLATE = "zippopotamRestTemplate";
    @Bean(ZIPPOPOTAM_REST_TEMPLATE)
    public RestTemplate zippopotamRestTemplate(
            @Value("${zippopotam.base-url}") String zippopotamUrl, RestTemplateBuilder builder) {

        return builder.rootUri(zippopotamUrl).build();
    }
}
