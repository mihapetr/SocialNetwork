package com.mihapetr.socialnetwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TestRestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
