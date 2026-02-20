package com.kyouseipro.neo.common.file.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConstructionFileGroupEntity {

    private Long groupId;
    private Long constructionId;

    private String groupName;
    private LocalDate workDate;

    private Integer displayOrder;
    private String note;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    private List<ConstructionFileEntity> files;
}