package com.kyouseipro.neo.common.file.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConstructionFileEntity {

    private Long fileId;

    private Long groupId;
    private Long constructionId;

    private String storedName;
    private String originalName;
    private String displayName;

    private String fileType;   // IMAGE / PDF / OTHER
    private String mimeType;

    private Long fileSize;

    private Integer width;
    private Integer height;

    private Integer displayOrder;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}