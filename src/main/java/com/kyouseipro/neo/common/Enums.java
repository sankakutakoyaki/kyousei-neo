package com.kyouseipro.neo.common;

import com.kyouseipro.neo.interfaces.CodeEnum;

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
        FACILITY(4, "施設"),
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
        NULL(0, ""),
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

    /**
     * 有無
     * 
     * @return 0.なし　1.有り
     */
    public enum yesOrNo implements CodeEnum {
        NO(0, ""),
        YES(1, "有り");

        private int num;
        private String str;

        private yesOrNo(int num, String str) {
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
            return Utilities.enumValueOf(Enums.yesOrNo.class, num).getDescription();
        }
    }

    /**
     * 自社営業所
     * 
     * @return 0.本社　1.大阪　2.和歌山　3.尼崎
     */
    public enum ownOffice implements CodeEnum {
        MAIN(0, "本社"),
        OSAKA(1, "大阪"),
        WAKAYAMA(2, "和歌山"),
        AMAGASAKI(3, "尼崎");

        private int num;
        private String str;

        private ownOffice(int num, String str) {
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
            return Utilities.enumValueOf(Enums.yesOrNo.class, num).getDescription();
        }
    }

    public final class HistoryTables {
        public static final String EMPLOYEES = "employees";
        public static final String COMPANIES = "companies";
        public static final String OFFICES = "offices";
        public static final String STAFFS = "staffs";
        public static final String PAIDHOLIDAY = "paid_holiday";
        public static final String TIMEWORKS = "timeworks";
        public static final String WORKINGCONDITIONS = "working_conditions";
        public static final String QUALIFICATIONS = "qualifications";
        public static final String RECYCLES = "recycles";
        public static final String RECYCLEMAKERS = "recycle_makers";
        public static final String RECYCLEITEMS = "recycle_items";
        public static final String WORKITEMS = "work_items";
        public static final String WORKPRICIES = "work_priceis";
        public static final String ORDERS = "orders";
        public static final String ORDERITEMS = "order_items";
    }

    /**
     * 勤怠の業務タイプ
     */
    public enum timeworksType implements CodeEnum {
        NORMAL(0, "通常勤務"),
        AUTO_START(1, "出勤自動補完"),
        AUTO_END(2, "退勤自動補完"),
        NIGHT(3, "夜勤"),
        PAID_LEAVE(10, "有給休暇"),
        SPECIAL_LEAVE(11, "特別休暇");

        private int num;
        private String str;

        private timeworksType(int num, String str) {
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
            for (timeworksType t  : values()) {
                if (t.num == num) {
                    return t.str;
                }
            }
            return null;
        }

        public static timeworksType from(int code) {
            for (timeworksType t : values()) {
                if (t.num == code) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown WorkType: " + code);
        }
    }

    /**
     * 打刻の種類
     */
    public enum timeworksCategory implements CodeEnum {
        START(1, "start"),
        END(2, "end");

        private int num;
        private String str;

        private timeworksCategory(int num, String str) {
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
            for (timeworksCategory c : values()) {
                if (c.num == num) {
                    return c.str;
                }
            }
            return null;
        }

        public static timeworksCategory from(String value) {
            for (timeworksCategory c : values()) {
                if (c.str.equalsIgnoreCase(value)) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Unknown TimeCategory: " + value);
        }

        /** ★ JSON → enum 変換用 */
        @com.fasterxml.jackson.annotation.JsonCreator
        public static timeworksCategory fromJson(String value) {
            for (timeworksCategory c : values()) {
                if (c.str.equalsIgnoreCase(value)
                    || c.name().equalsIgnoreCase(value)) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Unknown timeworksCategory: " + value);
        }
    }

    // public final class TimeworksState {
    //     public static final int WORKING = 0;
    //     public static final int FINISHED = 1;
    //     public static final int LEAVE = 2;
    // }
    /**
     * 勤怠データの状態
     */
    public enum timeworksState implements CodeEnum {
        NOT_STARTED(0, "未打刻"),
        WORKING(1, "勤務中"),
        FINISHED(2, "勤務終了"),
        FIXED(3, "確定済");

        private int num;
        private String str;

        private timeworksState(int num, String str) {
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
            for (timeworksState s : values()) {
                if (s.num == num) {
                    return s.str;
                }
            }
            return null;
        }

        public static timeworksState from(int code) {
            for (timeworksState s : values()) {
                if (s.num == code) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Unknown TimeworksState: " + code);
        }
    }
}