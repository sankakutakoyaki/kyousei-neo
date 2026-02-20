package com.kyouseipro.neo.common.file.controller;

import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/files")
public class FileDownloadController {

    private static final String BASE_PATH = "D:/files/";

    @GetMapping("/{constructionId}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable Long constructionId,
            @PathVariable String filename
    ) throws IOException {

        Path path = Paths.get(BASE_PATH,
                String.valueOf(constructionId),
                filename);

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(path.toUri());

        String mime = Files.probeContentType(path);
        if (mime == null) {
            mime = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mime))
                .body(resource);
    }
}