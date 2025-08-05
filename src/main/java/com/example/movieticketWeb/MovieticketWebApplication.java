package com.example.movieticketWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
        info =
                @Info(
                        title = "Movie ticket REST API Documentation",
                        description = "Movie ticket REST API Documentation",
                        version = "v1",
                        contact =
                                @Contact(
                                        name = "Nguyen Sang Huy",
                                        email = "nguyensanghuypy@gmail.com",
                                        url = "https://www.eazybytes.com"),
                        license = @License(name = "Apache 2.0", url = "https://www.eazybytes.com")),
        externalDocs =
                @ExternalDocumentation(
                        description = "Movie ticket REST API Documentation",
                        url = "https://www.eazybytes.com/swagger-ui.html"))
public class MovieticketWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieticketWebApplication.class, args);
    }
}
