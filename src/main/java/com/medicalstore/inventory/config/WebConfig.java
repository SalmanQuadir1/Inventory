package com.medicalstore.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        exposeDirectory("uploads", registry);
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        if (dirName.startsWith("../"))
            dirName = dirName.replace("../", "");

        // Correctly format for Windows to handle spaces and drive letters
        String location = uploadPath.replace("\\", "/");
        if (!location.startsWith("/")) {
            location = "/" + location;
        }

        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:" + location + "/");
    }
}
