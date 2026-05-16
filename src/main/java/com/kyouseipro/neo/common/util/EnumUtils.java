package com.kyouseipro.neo.common.util;

import java.util.Arrays;

import com.kyouseipro.neo.interfaces.enums.CodeEnum;

public class EnumUtils {
    /**
     * 第一引数に指定されたEnumの中から、第2引数のコード値と一致するものを取得する。
     *
     * @param target 取得したいEnumのクラス
     * @param code   検索するコード値
     * @param <E>    CodeInterfaceを実装したEnumクラス
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <E extends Enum & CodeEnum> E enumValueOf(Class<E> target, int num) {
        return Arrays.stream(target.getEnumConstants())
                .filter(data -> data.getCode() == num)
                .findFirst()
                .orElse(null);
    }

}
