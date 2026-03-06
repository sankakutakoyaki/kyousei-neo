package com.kyouseipro.neo.qualification.entity;

import java.util.ArrayList;
import java.util.List;

import com.kyouseipro.neo.common.file.entity.FileDto;

import lombok.Data;

@Data
public class QualificationDto {

    private Long qualificationsId;
    private String qualificationName;
    private List<FileDto> files = new ArrayList<>();
}
