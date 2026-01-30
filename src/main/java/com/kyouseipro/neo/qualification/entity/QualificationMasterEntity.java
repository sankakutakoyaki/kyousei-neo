package com.kyouseipro.neo.qualification.entity;

import lombok.Data;

@Data
public class QualificationMasterEntity {

    private int qualificationMasterId;
    private int code;
    private String name;
    private String categoryName;
    private String organization;
    private int validityYears;
    private int state;
}
