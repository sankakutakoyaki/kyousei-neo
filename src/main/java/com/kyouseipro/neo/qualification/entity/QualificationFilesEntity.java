package com.kyouseipro.neo.qualification.entity;

import com.kyouseipro.neo.interfaces.FileUpload;

import lombok.Data;

@Data
public class QualificationFilesEntity implements FileUpload {
    private int qualificationsFilesId;
    private int qualificationsId;
    private String fileName;
    private String internalName;
    private String folderName;
    private int version;
    private int state;

    @Override
    public void setFileName(String file_name) {
        this.fileName = file_name;
    }

    @Override
    public void setInternalName(String internal_name) {
        this.internalName = internal_name;
    }

    @Override
    public void setFolderName(String folder_name) {
        this.folderName = folder_name;
    }

    @Override
    public QualificationFilesEntity create() {
        return new QualificationFilesEntity();
    }
}
