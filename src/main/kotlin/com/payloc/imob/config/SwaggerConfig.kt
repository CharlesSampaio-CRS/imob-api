package com.payloc.imob.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Tenant API")
                    .version("1.0")
                    .description("API documentation for Tenant management")
            )
    }

    @Bean
    fun tenantApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("tenants")
            .pathsToMatch("/api/tenant/**")
            .build()
    }
}