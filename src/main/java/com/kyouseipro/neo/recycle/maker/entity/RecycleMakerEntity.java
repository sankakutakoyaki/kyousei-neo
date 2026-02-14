package com.kyouseipro.neo.recycle.maker.entity;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class RecycleMakerEntity implements CsvExportable {
    private int recycleMakerId;
    private int code;
    private String name;
    private String abbrName;
    private int group;
    private String groupName;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,コード,製造業者等名,略称,グループ名";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(recycleMakerId) + "," +
               Utilities.escapeCsv(code) + "," +
               Utilities.escapeCsv(name) + "," +
               Utilities.escapeCsv(groupName);
    }
}
