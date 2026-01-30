package com.kyouseipro.neo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UploadConfig {

    private static String uploadDir;

    @Value("${upload.path}")
    public void setUploadPath(String path) {
        UploadConfig.uploadDir = path;
    }

    public static String getUploadDir() {
        return uploadDir;
    }
}