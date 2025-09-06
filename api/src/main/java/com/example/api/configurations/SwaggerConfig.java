package com.example.api.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ElectroStore API")
                        .description("API for managing products, deals, baskets and receipts")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tong Duong")
                                .email("tongduong0710@gmail.com"))
                );
    }
}
