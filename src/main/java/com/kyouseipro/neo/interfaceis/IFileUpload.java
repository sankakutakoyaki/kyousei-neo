package com.kyouseipro.neo.interfaceis;

public interface IFileUpload {
    void setFileName(String file_name);
    void setInternalName(String internal_name);
    void setFolderName(String folder_name);
    String getInsertString();
}
