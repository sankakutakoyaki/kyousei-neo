package com.kyouseipro.neo.interfaces;

import java.util.List;

import com.kyouseipro.neo.common.file.entity.FileDto;

public interface FileAttachable {
    Long getId();
    void setFiles(List<FileDto> files);
}
