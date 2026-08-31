package com.kyouseipro.neo.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UploadConfig {

    private final Path uploadDirectory;

    public UploadConfig(@Value("${upload.path}") String uploadPath) {
        this.uploadDirectory = Paths.get(uploadPath).toAbsolutePath().normalize();
    }

    public Path getUploadDirectory() {
        return uploadDirectory;
    }
}
