package com.kyouseipro.neo.common.file.abstruct;

import lombok.Data;

@Data
public abstract class BaseFileEntity {
    protected String fileName;
    protected String internalName;
    protected String folderName;
}
