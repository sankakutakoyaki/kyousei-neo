package com.kyouseipro.neo.common;

import java.util.Arrays;
import java.util.List;

import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.interfaceis.IEnum;

public class Utilities {
    /**
     * 第一引数に指定されたEnumの中から、第2引数のコード値と一致するものを取得する。
     *
     * @param target 取得したいEnumのクラス
     * @param code   検索するコード値
     * @param <E>    CodeInterfaceを実装したEnumクラス
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <E extends Enum & IEnum> E enumValueOf(Class<E> target, int num) {
        return Arrays.stream(target.getEnumConstants())
                .filter(data -> data.getNum() == num)
                .findFirst()
                .orElse(null);
    }

    /**
     * IN句用の数列を作成する
     * @param list
     * @return
     */
    public static String createSequenceByIds(List<SimpleData> list) {
        StringBuilder idsStr = new StringBuilder();
        list.forEach(value -> {
            idsStr.append(value.getNumber()).append(", ");
        });
        return idsStr.substring(0, idsStr.length() - 2);
    }
}
