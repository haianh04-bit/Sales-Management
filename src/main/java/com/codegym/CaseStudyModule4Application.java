package com.codegym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CaseStudyModule4Application implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(CaseStudyModule4Application.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/resources/uploads/**")
                .addResourceLocations("file:" + "D:/module4/case-study-module4/src/main/resources/uploads/")
                .setCachePeriod(0);
    }
}

