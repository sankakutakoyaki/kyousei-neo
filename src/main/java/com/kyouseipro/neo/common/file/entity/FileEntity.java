package com.kyouseipro.neo.common.file.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FileEntity {
    private Long fileId;
    private Long groupId;
    private Long parentId;
    private String parentType;
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
    private int state;
}
