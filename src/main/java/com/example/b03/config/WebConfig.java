package com.example.b03.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // URL: /uploads/xxx.pdf → 실제 파일 경로: C:/upload/xxx.pdf
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/upload/");
    }
}