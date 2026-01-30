package com.kyouseipro.neo.qualification.entity;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class QualificationsEntity implements CsvExportable {
    private int qualificationsId;
    private int ownerId;
    private int ownerCategory;
    private String ownerName;
    private String ownerNameKana;
    private int qualificationMasterId;
    private String qualificationName;
    private String number;
    private LocalDate acquisitionDate = LocalDate.of(9999, 12, 31);
    private LocalDate expiryDate = LocalDate.of(9999, 12, 31);
    private int version;
    private int state;

    private String status;
    private int isEnabled;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "所有者ID,所有者,資格名,資格番号,有効期限";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(ownerId)) + "," +
               Utilities.escapeCsv(ownerName) + "," +
               Utilities.escapeCsv(qualificationName) + "," +
               Utilities.escapeCsv(number) + "," +
               Utilities.escapeCsv(String.valueOf(expiryDate));
    }
}

