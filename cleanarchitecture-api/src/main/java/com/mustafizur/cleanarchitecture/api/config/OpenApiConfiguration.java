package com.mustafizur.cleanarchitecture.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfiguration {
    @Bean
    OpenAPI cleanArchitectureOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Clean Architecture DDD API")
                .version("v1")
                .description("Java/Spring Boot Clean Architecture + DDD reference API"));
    }
}
