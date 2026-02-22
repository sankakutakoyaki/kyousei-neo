package com.kyouseipro.neo.common.file.entity;

import lombok.Data;

@Data
public class FileDto {
    private Long fileId;
    private Long groupId;
    private String displayName;
    private String groupName;
}
