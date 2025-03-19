package com.example.movieticketWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MovieticketWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieticketWebApplication.class, args);
	}

}
