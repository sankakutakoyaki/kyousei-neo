package com.kyouseipro.neo.domain.operations;

import java.util.*;
import com.kyouseipro.neo.common.exception.BusinessException;

public enum OperationSpec {
    VEHICLE("vehicles", "車両管理", "運行", List.of(
            new Field("code", "車両ID", "text", false),
            new Field("ownOfficeId", "営業所", "office", false),
            new Field("name", "車両名", "text", true),
            new Field("plateNumber", "ナンバー", "text", true),
            new Field("vehicleType", "車種", "vehicleType", true),
            new Field("capacity", "乗車定員", "number", true),
            new Field("recorderId", "ドラレコ車両ID", "text", false),
            new Field("remarks", "備考", "textarea", false))),
    INSPECTION("vehicle-maintenance", "点検予定・実施履歴", "運行", List.of(
            new Field("vehicleCode", "車両コード", "text", true),
            new Field("kind", "点検種別（車検・年次点検等）", "text", true),
            new Field("dueDate", "期限", "date", false),
            new Field("scheduledDate", "実施予定日", "date", false),
            new Field("performedDate", "実施日", "date", false),
            new Field("unavailableFrom", "使用不可（開始日）", "date", false),
            new Field("unavailableTo", "使用不可（終了日）", "date", false),
            new Field("odometer", "走行距離（km）", "number", false),
            new Field("cost", "整備費用（円）", "number", false),
            new Field("remarks", "整備内容・備考", "text", false))),
    CREW("daily-crews", "日別の乗車編成", "運行", List.of(
            new Field("workDate", "運行日", "date", true),
            new Field("vehicleCode", "車両コード", "text", true),
            new Field("remarks", "備考", "text", false))),
    SCORE("driving-records", "運転記録", "運行", List.of(
            new Field("workDate", "運行日", "date", true),
            new Field("vehicleCode", "車両コード", "text", true),
            new Field("employeeCode", "ドライバーコード", "text", true),
            new Field("score", "運転得点（未入力可）", "number", false),
            new Field("remarks", "備考・運転交代の補足", "text", false))),
    QUALIFICATION_TYPE("qualification-types", "資格種類", "登録", List.of(
            new Field("code", "資格コード", "text", true),
            new Field("name", "資格・教育・免許の名称", "text", true),
            new Field("category", "区分（免許・資格・講習・教育）", "text", true),
            new Field("grade", "等級・種別", "text", false),
            new Field("expiryRequired", "有効期限あり", "checkbox", false),
            new Field("driverLicense", "運転免許に該当", "checkbox", false),
            new Field("remarks", "備考", "text", false))),
    QUALIFICATION("qualifications", "資格管理", "管理", List.of(
            new Field("employeeCode", "担当者コード", "text", true),
            new Field("qualificationCode", "資格コード", "text", true),
            new Field("acquiredDate", "取得・受講日", "date", true),
            new Field("expiryDate", "有効期限", "date", false),
            new Field("renewalDate", "次回更新・講習予定日", "date", false),
            new Field("certificateNumber", "資格番号", "text", false),
            new Field("remarks", "備考", "text", false))),
    LABOR("labor", "労務情報", "管理", List.of(
            new Field("employeeCode", "担当者コード", "text", true),
            new Field("effectiveFrom", "適用開始日", "date", true),
            new Field("effectiveTo", "適用終了日", "date", false),
            new Field("healthInsurance", "健康保険の加入区分・名称", "text", false),
            new Field("healthLastFour", "健康保険番号（末尾4桁以内）", "text", false),
            new Field("pension", "年金の加入区分", "text", false),
            new Field("employmentInsurance", "雇用保険の加入区分", "text", false),
            new Field("employmentLastFour", "雇用保険番号（末尾4桁以内）", "text", false),
            new Field("retirementBook", "建退共手帳（有・無・未確認）", "text", false),
            new Field("confirmedDate", "情報確認日", "date", false),
            new Field("remarks", "備考", "text", false))),
    HEALTH("health-checks", "健康診断履歴", "管理", List.of(
            new Field("employeeCode", "担当者コード", "text", true),
            new Field("examDate", "健康診断日", "date", true),
            new Field("examType", "診断種別（一般・特殊）", "text", true),
            new Field("nextDate", "次回予定日", "date", false),
            new Field("systolic", "血圧（最高）", "number", false),
            new Field("diastolic", "血圧（最低）", "number", false),
            new Field("remarks", "特殊健診の種類・備考", "text", false)));
    public static final List<String> VEHICLE_TYPES=List.of("トラック","軽バン","軽トラック","バン","乗用車","その他");
    public record Field(String name, String label, String type, boolean required) {}
    public final String route, title, section;
    public final List<Field> fields;
    OperationSpec(String route,String title,String section,List<Field> fields) { this.route=route; this.title=title; this.section=section; this.fields=fields; }
    public String table() { return "operation_" + name().toLowerCase(Locale.ROOT); }
    public boolean sensitive() { return this==LABOR || this==HEALTH; }
    public static OperationSpec from(Object value) {
        try { return valueOf(String.valueOf(value)); } catch(RuntimeException e) { throw new BusinessException("画面の種類が不正です。"); }
    }
    public static String snake(String value) { return value.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT); }
    public List<String> columns() {
        var columns=new ArrayList<String>(List.of("id", "version", "state", "regist_date", "update_date"));
        fields.forEach(f -> columns.add(snake(f.name())));
        if (fields.stream().anyMatch(f -> f.name().equals("vehicleCode"))) columns.add("vehicle_id");
        if (fields.stream().anyMatch(f -> f.name().equals("employeeCode"))) { columns.add("employee_id"); columns.add("employee_name"); }
        if(this==QUALIFICATION) { columns.add("qualification_type_id"); columns.add("qualification_name"); }
        return columns;
    }
}
