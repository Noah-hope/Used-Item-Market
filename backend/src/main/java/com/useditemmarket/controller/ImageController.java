package com.useditemmarket.controller;

import com.useditemmarket.util.ProjectPaths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    private final ServletContext servletContext;

    public ImageController(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @GetMapping("/img/{uid}/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String uid,
                                               @PathVariable String filename) throws IOException {
        Path imagePath = Paths.get(ProjectPaths.resolveImageBaseDir().getAbsolutePath(), uid, filename)
                .normalize()
                .toAbsolutePath();

        Path basePath = ProjectPaths.resolveImageBaseDir().toPath().toAbsolutePath().normalize();
        if (!imagePath.startsWith(basePath) || !Files.exists(imagePath) || !Files.isRegularFile(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(imagePath.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = detectContentType(imagePath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private String detectContentType(Path imagePath) throws IOException {
        String contentType = Files.probeContentType(imagePath);
        if (contentType != null) {
            return contentType;
        }

        String fallback = servletContext.getMimeType(imagePath.getFileName().toString());
        return fallback != null ? fallback : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
