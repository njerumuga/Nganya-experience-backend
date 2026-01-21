package com.nganyaexperience.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Events / general uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // Nganyas uploads (persistent)
        registry.addResourceHandler("/uploads/nganyas/**")
                .addResourceLocations("file:/mnt/data/uploads/nganyas/");
    }
}
