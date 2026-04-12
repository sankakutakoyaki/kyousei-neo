package com.kyouseipro.neo.common.enums.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kyouseipro.neo.common.master.combo.ComboDto;
import com.kyouseipro.neo.interfaces.BaseEnum;

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
}