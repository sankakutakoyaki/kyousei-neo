package com.kyouseipro.neo.query.employee;

import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.service.FileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeQueryBuilder {
    private final FileService fileService;

    public String getCsvString(List<Entity> items) {
        StringBuilder sb = new StringBuilder();

        // ヘッダー
        sb.append("ID,");
        sb.append("コード,");
        sb.append("姓,");
        sb.append("名,");
        sb.append("せい,");
        sb.append("めい,");
        sb.append("携帯番号,");
        sb.append("郵便番号,");
        sb.append("住所,");
        sb.append("メールアドレス,");
        sb.append("性別,");
        sb.append("血液型,");
        sb.append("生年月日,");
        sb.append("緊急連絡先,");
        sb.append("緊急連絡先番号");
        sb.append("\n");

        for (Entity item : items) {
            EmployeeEntity entity = (EmployeeEntity) item;
            sb.append(fileService.toCsvField(entity.getEmployee_id()));
            sb.append(fileService.toCsvField(entity.getCode()));
            sb.append(fileService.toCsvField(entity.getLast_name()));
            sb.append(fileService.toCsvField(entity.getFirst_name()));
            sb.append(fileService.toCsvField(entity.getLast_name_kana()));
            sb.append(fileService.toCsvField(entity.getFirst_name_kana()));
            sb.append(fileService.toCsvField(entity.getPhone_number()));
            sb.append(fileService.toCsvField(entity.getPostal_code()));
            sb.append(fileService.toCsvField(entity.getFull_address()));
            sb.append(fileService.toCsvField(entity.getEmail()));
            sb.append(fileService.toCsvField(Enums.gender.getDescriptionByNum(entity.getGender())));
            sb.append(fileService.toCsvField(Enums.bloodType.getDescriptionByNum(entity.getBlood_type())));
            sb.append(fileService.toCsvField(entity.getBirthday()));
            sb.append(fileService.toCsvField(entity.getEmergency_contact()));
            sb.append(fileService.toCsvField(entity.getEmergency_contact_number()));
            sb.append("\n");
        }

        return sb.toString();
    }
}
