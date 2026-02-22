package com.kyouseipro.neo.common.file.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class FileGroupEntity {
    private Long groupId;
    private String parentType;
    private Long parentId;
    private String groupName;
    private LocalDate workDate;
    private Integer displayOrder;
    private String note;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private int state;

    private List<FileEntity> files;
}
