package com.kyouseipro.neo.qualification.entity;

import java.util.ArrayList;
import java.util.List;

import com.kyouseipro.neo.common.file.entity.FileDto;
import com.kyouseipro.neo.interfaces.FileAttachable;

import lombok.Data;

@Data
public class QualificationsDto implements FileAttachable {

    private Long qualificationsId;
    private String number;
    private String qualificationName;
    private List<FileDto> files = new ArrayList<>();

    @Override
    public Long getId() {
        return qualificationsId;
    }

    @Override
    public void setFiles(List<FileDto> files) {
        this.files = files;
    }

}