package com.useditemmarket.config;

import com.useditemmarket.util.ProjectPaths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class ImageResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + ProjectPaths.resolveImageBaseDir().getAbsolutePath().replace(File.separatorChar, '/') + "/";
        registry.addResourceHandler("/img/**")
                .addResourceLocations(location);
    }
}
