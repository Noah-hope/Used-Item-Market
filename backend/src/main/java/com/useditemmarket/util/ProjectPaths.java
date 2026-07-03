package com.useditemmarket.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ProjectPaths {

    private ProjectPaths() {
    }

    public static File resolveImageBaseDir() {
        return new File(resolveProjectRoot(), "img");
    }

    public static File resolveProjectRoot() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            Path basePath = Paths.get(catalinaBase).toAbsolutePath().normalize();
            Path backendPath = basePath.getParent();
            if (backendPath != null && "backend".equalsIgnoreCase(String.valueOf(backendPath.getFileName()))) {
                Path projectRoot = backendPath.getParent();
                if (projectRoot != null) {
                    return projectRoot.toFile();
                }
            }
        }

        File workingDir = new File(System.getProperty("user.dir")).getAbsoluteFile();
        if ("backend".equalsIgnoreCase(workingDir.getName()) && workingDir.getParentFile() != null) {
            return workingDir.getParentFile();
        }
        return workingDir;
    }
}
