package com.useditemmarket.service.support;

import com.useditemmarket.exception.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    ));

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * Save an uploaded image to webapp/img/{uid}/{UUID}.{ext}
     * @return path like "/img/{uid}/{uuid}.{ext}"
     */
    public String saveImage(MultipartFile file, String uid) {
        if (file == null || file.isEmpty()) {
            throw new BaseException(400, "璇烽€夋嫨鍥剧墖鏂囦欢");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BaseException(400, "鍥剧墖澶у皬涓嶈兘瓒呰繃5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BaseException(400, "涓嶆敮鎸佺殑鍥剧墖鏍煎紡锛屼粎鏀寔 jpg銆乯peg銆乸ng銆乬if銆亀ebp");
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        File baseDir = resolveBaseDir();
        File userDir = new File(baseDir, uid);
        if (!userDir.exists()) {
            if (!userDir.mkdirs()) {
                throw new BaseException(500, "鍒涘缓鍥剧墖瀛樺偍鐩綍澶辫触");
            }
        }

        File dest = new File(userDir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BaseException(500, "鍥剧墖淇濆瓨澶辫触");
        }

        return "/img/" + uid + "/" + fileName;
    }

    private File resolveBaseDir() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            ServletContext sc = attrs.getRequest().getServletContext();
            String realPath = sc.getRealPath("/img");
            if (realPath != null) {
                return new File(realPath);
            }
        } catch (Exception ignored) {
        }

        // Fallback for dev: user.dir is the project root (backend/)
        return new File(System.getProperty("user.dir"), "src/main/webapp/img");
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
