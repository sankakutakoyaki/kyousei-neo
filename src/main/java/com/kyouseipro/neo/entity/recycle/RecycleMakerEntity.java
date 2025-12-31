package com.kyouseipro.neo.entity.recycle;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class RecycleMakerEntity implements CsvExportable {
    private int recycle_maker_id;
    private int code;
    private String name;
    private String abbr_name;
    private int group;
    private String group_name;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,コード,製造業者等名,略称,グループ名";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(recycle_maker_id)) + "," +
               Utilities.escapeCsv(String.valueOf(code)) + "," +
               Utilities.escapeCsv(name) + "," +
               Utilities.escapeCsv(group_name);
    }
}
