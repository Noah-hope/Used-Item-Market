package com.useditemmarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class ImageResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File workingDir = new File(System.getProperty("user.dir"));
        File projectRoot = workingDir;
        if ("backend".equalsIgnoreCase(workingDir.getName()) && workingDir.getParentFile() != null) {
            projectRoot = workingDir.getParentFile();
        }

        String location = new File(projectRoot, "img").toURI().toString();
        registry.addResourceHandler("/img/**")
                .addResourceLocations(location);
    }
}
