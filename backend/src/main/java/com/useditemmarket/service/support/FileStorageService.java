package com.useditemmarket.service.support;

import com.useditemmarket.exception.BaseException;
import com.useditemmarket.util.ProjectPaths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    ));

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * Save an uploaded image to {projectRoot}/img/{uid}/{UUID}.{ext}
     * @return path like "/img/{uid}/{uuid}.{ext}"
     */
    public String saveImage(MultipartFile file, String uid) {
        if (file == null || file.isEmpty()) {
            throw new BaseException(400, "请选择图片文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BaseException(400, "图片大小不能超过 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BaseException(400, "不支持的图片格式，仅支持 jpg、jpeg、png、gif、webp");
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        File baseDir = resolveBaseDir();
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            log.error("无法创建图片根目录: {}", baseDir.getAbsolutePath());
            throw new BaseException(500, "无法创建图片目录");
        }
        File userDir = new File(baseDir, uid);
        if (!userDir.exists()) {
            if (!userDir.mkdirs()) {
                log.error("无法创建用户图片目录: {}", userDir.getAbsolutePath());
                throw new BaseException(500, "无法创建图片目录");
            }
        }

        File dest = new File(userDir, fileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("图片保存失败, path={}", dest.getAbsolutePath(), e);
            throw new BaseException(500, "图片保存失败");
        }

        return "/img/" + uid + "/" + fileName;
    }

    public void deleteImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return;
        }

        String normalized = imagePath.trim().replace('\\', '/');
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        File file = new File(ProjectPaths.resolveProjectRoot(), normalized);
        try {
            if (file.exists() && !file.delete()) {
                log.warn("删除图片失败: {}", file.getAbsolutePath());
            }
        } catch (Exception ex) {
            log.warn("删除图片异常: {}", file.getAbsolutePath(), ex);
        }
    }

    private File resolveBaseDir() {
        File baseDir = ProjectPaths.resolveImageBaseDir();
        log.info("图片保存目录: {}", baseDir.getAbsolutePath());
        return baseDir;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
