package com.kyouseipro.neo.interfaces;

public interface FileUpload {
    void setFileName(String fileName);
    void setInternalName(String internalName);
    void setFolderName(String folderName);
    FileUpload create();
}
