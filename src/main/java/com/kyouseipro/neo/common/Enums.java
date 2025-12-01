package com.kyouseipro.neo.common;

import com.kyouseipro.neo.interfaceis.CodeEnum;

public class Enums {
    /**
     * DBレコード状態
     * 
     * @return 0.新規(CREATE) 1.更新(UPDATE) 2.削除(DELETE) 3.完了(COMPLETE)
     */
    public enum state implements CodeEnum {
        CREATE(0, "新規"),
        UPDATE(1, "更新"),
        DELETE(2, "削除"),
        COMPLETE(3, "完了");

        private int num;
        private String str;

        private state(int num, String str) {
            this.num = num;
            this.str = str;
        }

        @Override
        public int getCode() {
            return this.num;
        }

        @Override
        public String getDescription() {
            return this.str;
        }

        public static String getDescriptionByNum(int num) {
            return Utilities.enumValueOf(Enums.state.class, num).getDescription();
        }
    }

    /**
     * 取引先カテゴリー
     * 
     * @return 1.施工(PARTNER) 2.荷主(CLIENT) 3.購買(SUPPLIER) 4.運送(TRANSPORT)
     */
    public enum clientCategory implements CodeEnum {
        PARTNER(1, "施工"),
        SHIPPER(2, "荷主"),
        SUPPLIER(3, "購買"),
        SERVICE(4, "サービス"),
        TRANSPORT(5, "運送");

        private int num;
        private String str;

        private clientCategory(int num, String str) {
            this.num = num;
            this.str = str;
        }

        @Override
        public int getCode() {
            return this.num;
        }

        @Override
        public String getDescription() {
            return this.str;
        }

        public static String getDescriptionByNum(int num) {
            return Utilities.enumValueOf(Enums.clientCategory.class, num).getDescription();
        }
    }

    /**
     * 従業員カテゴリー
     * 
     * @return 1.社員 2.アルバイト 3.請負
     */
    public enum employeeCategory implements CodeEnum {
        FULLTIME(1, "社員"),
        PARTTIME(2, "アルバイト"),
        CONSTRUCT(3, "請負");

        private int num;
        private String str;

        private employeeCategory(int num, String str) {
            this.num = num;
            this.str = str;
        }

        @Override
        public int getCode() {
            return this.num;
        }

        @Override
        public String getDescription() {
            return this.str;
        }

        public static String getDescriptionByNum(int num) {
            return Utilities.enumValueOf(Enums.employeeCategory.class, num).getDescription();
        }
    }

    /**
     * 性別
     * 
     * @return 1:男(MAN) 2:女(WOMAN) 9:無回答(OTHERS)
     */
    public enum gender implements CodeEnum {
        NULL(0, ""),
        MAN(1, "男性"),
        WOMAN(2, "女性"),
        OTHERS(9, "無回答");

        private int num;
        private String str;

        private gender(int num, String str) {
            this.num = num;
            this.str = str;
        }

        @Override
        public int getCode() {
            return this.num;
        }

        @Override
        public String getDescription() {
            return this.str;
        }

        public static String getDescriptionByNum(int num) {
            return Utilities.enumValueOf(Enums.gender.class, num).getDescription();
        }
    }

    /**
     * 血液型
     *
     * @return 1:A型(A) 2:B型(B) 3:O型(O) 4:AB型(AB) 9:無回答(OTHERS)
     */
    public enum bloodType implements CodeEnum {
        NULL(0, ""),
        A(1, "A型"),
        B(2, "B型"),
        O(3, "O型"),
        AB(4, "AB型"),
        OTHERS(9, "無回答");

        private int num;
        private String str;

        private bloodType(int num, String str) {
            this.num = num;
            this.str = str;
        }

        @Override
        public int getCode() {
            return this.num;
        }

        @Override
        public String getDescription() {
            return this.str;
        }

        public static String getDescriptionByNum(int num) {
            return Utilities.enumValueOf(Enums.bloodType.class, num).getDescription();
        }
    }

    /**
     * 曜日
     *
     * @return 0:日 1:月 2:火 3:水 4:木 5:金 6:土
     */
    public enum dayOfWeekToStr implements CodeEnum {
        SUNDAY(0, "日"),
        MONDY(1, "月"),
        TUESDAY(2, "火"),
        WEDNESDAY(3, "水"),
        THURSDAY(4, "木"),
        FRIDAY(5, "金"),
        SATURDSY(6, "土");

        private int num;
        private String str;

        private dayOfWeekToStr(int num, String str) {
            this.num = num;
            this.str = str;
        }

        @Override
        public int getCode() {
            return this.num;
        }

        @Override
        public String getDescription() {
            return this.str;
        }

        public static String getDescriptionByNum(int num) {
            return Utilities.enumValueOf(Enums.dayOfWeekToStr.class, num).getDescription();
        }
    }

    /**
     * 支払い形態
     * 
     * @return 1.月給 2.日払い
     */
    public enum paymentMethod implements CodeEnum {
        NULL(0, ""),
        MONTHLY(1, "月給"),
        DAILY(2, "日払い");

        private int num;
        private String str;

        private paymentMethod(int num, String str) {
            this.num = num;
            this.str = str;
        }

        @Override
        public int getCode() {
            return this.num;
        }

        @Override
        public String getDescription() {
            return this.str;
        }

        public static String getDescriptionByNum(int num) {
            return Utilities.enumValueOf(Enums.paymentMethod.class, num).getDescription();
        }
    }

    /**
     * 給与形態
     * 
     * @return 1.固定 2.歩合 3.時給
     */
    public enum payType implements CodeEnum {
        NULL(0, ""),
        FIXED(1, "固定"),
        COMMISSION(2, "歩合"),
        HOURLY(3, "時給");

        private int num;
        private String str;

        private payType(int num, String str) {
            this.num = num;
            this.str = str;
        }

        @Override
        public int getCode() {
            return this.num;
        }

        @Override
        public String getDescription() {
            return this.str;
        }

        public static String getDescriptionByNum(int num) {
            return Utilities.enumValueOf(Enums.payType.class, num).getDescription();
        }
    }

    /**
     * 商品項目分類
     * 
     * @return 1.商品 2.材料 3.備品 4.返品
     */
    public enum ItemClass implements CodeEnum {
        GOODS(1, "商品"),
        MATERIALS(2, "材料"),
        EQUIPMENT(3, "備品"),
        RETURNS(4, "返品");

        private int num;
        private String str;

        private ItemClass(int num, String str) {
            this.num = num;
            this.str = str;
        }

        @Override
        public int getCode() {
            return this.num;
        }

        @Override
        public String getDescription() {
            return this.str;
        }

        public static String getDescriptionByNum(int num) {
            return Utilities.enumValueOf(Enums.ItemClass.class, num).getDescription();
        }
    }
}