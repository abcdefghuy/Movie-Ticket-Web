package com.example.movieticketWeb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // Đường dẫn thư mục chứa ảnh
    public static final String uploadDirectory = "D:/project_uploads/movies";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**","/static/**")
                .addResourceLocations("file:///" + uploadDirectory + "/", "classpath:/static/");
    }
}