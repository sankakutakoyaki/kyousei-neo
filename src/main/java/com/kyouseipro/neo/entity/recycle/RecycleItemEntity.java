package com.kyouseipro.neo.entity.recycle;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class RecycleItemEntity implements CsvExportable {
    private int recycleItemId;
    private int code;
    private String name;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,コード,品目・料金区分";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(recycleItemId)) + "," +
               Utilities.escapeCsv(String.valueOf(code)) + "," +
               Utilities.escapeCsv(name);
    }
}
