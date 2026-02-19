package com.kyouseipro.neo.qualification.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class QualificationsEntityRequest {
    
    private Integer qualificationsId;
    private Integer employeeId;
    private Integer companyId;
    private String ownerName;
    private String ownerNameKana;
    private Integer qualificationMasterId;
    private String qualificationName;
    private String number;
    private LocalDate acquisitionDate;
    private LocalDate expiryDate;
    private Integer version;
    private Integer state;

    private String status;
    private Integer isEnabled;
}
