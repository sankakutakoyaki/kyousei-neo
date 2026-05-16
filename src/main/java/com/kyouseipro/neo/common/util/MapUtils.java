package com.kyouseipro.neo.common.util;

import java.util.Map;

public class MapUtils {
    /**
     * フロントから来た数値をLongに変換する
     */
    public class MapUtil {

        public static Long getLong(Map<String, Object> map, String key){
            Object v = map.get(key);
            return v == null ? null : ((Number)v).longValue();
        }

        public static Integer getInt(Map<String, Object> map, String key){
            Object v = map.get(key);
            return v == null ? null : ((Number)v).intValue();
        }

        public static String getString(Map<String, Object> map, String key){
            Object v = map.get(key);
            return v == null ? null : v.toString();
        }
    }
}
