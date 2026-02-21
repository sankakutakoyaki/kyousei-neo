package com.kyouseipro.neo.common.file.entity;

import lombok.Data;

@Data
public class FileMeta {
    private String storedName;
    private String originalName;
    private String mimeType;
    private Long fileSize;
    private Integer width;
    private Integer height;
}