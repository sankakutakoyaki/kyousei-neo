package com.kyouseipro.neo._backup;

public interface FileUpload {
    void setFileName(String fileName);
    void setInternalName(String internalName);
    void setFolderName(String folderName);
    FileUpload create();
}
