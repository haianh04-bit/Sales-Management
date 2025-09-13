package com.codegym.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ánh xạ URL /uploads/** tới thư mục resources/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/uploads/");

        // Nếu bạn lưu ra ngoài ổ cứng
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/Users/mac/Documents/Sales-Management/uploads/");
    }
}
