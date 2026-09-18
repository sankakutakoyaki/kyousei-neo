// package com.kyouseipro.neo.sql.model;

// import lombok.AllArgsConstructor;
// import lombok.Getter;

// @Getter
// @AllArgsConstructor
// public class CsvColumn {
//     private String key;
//     private String label;
// }

package com.kyouseipro.neo.sql.model;

import java.util.Arrays;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;

import lombok.Getter;

@Getter
public class CsvColumn {

    private final String key;
    private final String label;
    private final Class<? extends BaseEnum> enumClass;

    public CsvColumn(String key, String label) {
        this(key, label, null);
    }

    public CsvColumn(
            String key,
            String label,
            Class<? extends BaseEnum> enumClass) {

        this.key = key;
        this.label = label;
        this.enumClass = enumClass;
    }

    public Object format(Object value) {

        if (value == null || enumClass == null) {
            return value;
        }

        if (!(value instanceof Number number)) {
            return value;
        }

        int code = number.intValue();

        return Arrays.stream(enumClass.getEnumConstants())
            .filter(e -> e.getCode() == code)
            .map(BaseEnum::getLabel)
            .findFirst()
            .orElse("");
    }
}