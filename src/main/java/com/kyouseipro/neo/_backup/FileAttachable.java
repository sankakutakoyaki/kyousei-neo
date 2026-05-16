package com.kyouseipro.neo._backup;

import java.util.List;

import com.kyouseipro.neo._backup.file.entity.FileDto;

public interface FileAttachable {
    Long getId();
    void setFiles(List<FileDto> files);
}
