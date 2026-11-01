package com.kyouseipro.neo.common.enums.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.interfaces.enums.BaseEnum;

public class EnumUtil {

    public static <E extends Enum<E> & BaseEnum> E of(Class<E> enumClass, int code) {
        return Arrays.stream(enumClass.getEnumConstants())
            .filter(e -> e.getCode() == code)
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException("Enum not found: " + enumClass + ", code=" + code)
            );
    }

    public static <E extends Enum<E> & BaseEnum> List<ComboDto> toCombo(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
            .map(e -> new ComboDto(
                (long) e.getCode(),
                e.getLabel()
            ))
            .toList();
    }

    public static <E extends Enum<E> & BaseEnum> Map<String, Integer> toMap(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
            .collect(Collectors.toMap(
                Enum::name,
                BaseEnum::getCode
            ));
    }
    /** 文字列で保存する既存の項目はEnum名を値とする。数値の既存APIは変更しない。 */
    public static <E extends Enum<E> & BaseEnum> List<Map<String,String>> toNameCombo(Class<E> type) {
        return Arrays.stream(type.getEnumConstants()).map(e->Map.of("value",e.name(),"label",e.getLabel())).toList();
    }
    public static <E extends Enum<E> & BaseEnum> Map<String,String> toNameLabels(Class<E> type) {
        return Arrays.stream(type.getEnumConstants()).collect(Collectors.toMap(Enum::name,BaseEnum::getLabel));
    }
    public static <E extends Enum<E> & BaseEnum> Map<String,String> toCodeLabels(Class<E> type) {
        return Arrays.stream(type.getEnumConstants()).collect(Collectors.toMap(e->Integer.toString(e.getCode()),BaseEnum::getLabel));
    }
    public static <E extends Enum<E> & BaseEnum> E byName(Class<E> type,String name) {
        if(name==null)return null;
        try{return Enum.valueOf(type,name);}catch(IllegalArgumentException e){return null;}
    }

}