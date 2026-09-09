package com.kyouseipro.neo.domain.business.order.pdf.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.common.exception.BusinessException;

/** Only explicitly confirmed fields are allowed into the business tables. */
public record ConfirmedOrderCandidate(Map<String, Object> order, List<Map<String, Object>> items,
                                      List<Map<String, Object>> works) {
    public static ConfirmedOrderCandidate parse(Map<String, String> input, ObjectMapper mapper) {
        if (input == null) throw new BusinessException("確認内容を入力してください。");
        Map<String, Object> order = new LinkedHashMap<>();
        order.put("title", text(input.get("customerName"), "氏名", true));
        String postal = text(input.get("postalCode"), "郵便番号", false).replace("-", "");
        if (!postal.isEmpty() && !postal.matches("[0-9]{7}")) throw new BusinessException("郵便番号は7桁で入力してください。");
        order.put("postalCode", postal.isEmpty() ? "" : postal.substring(0, 3) + "-" + postal.substring(3));
        order.put("fullAddress", text(input.get("address"), "住所", true));
        order.put("contactInformation", text(input.get("mobilePhone"), "連絡先", false));
        order.put("remarks", text(input.get("contactNote"), "備考", false));
        String date = input.get("requestedDate") == null ? "" : input.get("requestedDate").trim();
        if (date.isEmpty()) order.put("visitDate", null);
        else {
            try {
                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) throw new DateTimeParseException("format", date, 0);
                order.put("visitDate", java.sql.Date.valueOf(LocalDate.parse(date)));
            } catch (DateTimeParseException | IllegalArgumentException error) {
                throw new BusinessException("配送工事日は年を確認し、YYYY-MM-DDで入力してください。未定の場合は空欄にしてください。");
            }
        }
        if (!input.containsKey("items") && ((input.get("itemModel1") != null && !input.get("itemModel1").isBlank())
                || (input.get("itemModel2") != null && !input.get("itemModel2").isBlank()))) {
            throw new BusinessException("画面を更新し、商品の数量を確認してから受注登録してください。");
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (JsonNode node : rows(input.get("items"), mapper, "商品")) {
            Map<String, Object> item = new LinkedHashMap<>();
            String name = text(value(node, "itemName"), "商品名", false);
            String model = text(value(node, "itemModel"), "型番", false);
            if (name.isEmpty() && model.isEmpty()) throw new BusinessException("商品の名称または型番を入力するか、不要な行を削除してください。");
            item.put("itemName", name);
            item.put("itemModel", model);
            item.put("itemQuantity", number(value(node, "itemQuantity"), "商品数量", true));
            items.add(item);
        }
        List<Map<String, Object>> works = new ArrayList<>();
        for (JsonNode node : rows(input.get("works"), mapper, "作業")) {
            Map<String, Object> work = new LinkedHashMap<>();
            work.put("orderWorkName", text(value(node, "orderWorkName"), "作業名", true));
            work.put("orderWorkQuantity", number(value(node, "orderWorkQuantity"), "作業数量", true));
            work.put("orderWorkPrice", number(value(node, "orderWorkPrice"), "作業単価", false));
            works.add(work);
        }
        return new ConfirmedOrderCandidate(order, items, works);
    }
    private static String value(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) return "";
        if (!value.isValueNode()) throw new BusinessException("明細の入力形式が不正です。");
        return value.asText();
    }
    private static JsonNode rows(String json, ObjectMapper mapper, String label) {
        try {
            JsonNode result = mapper.readTree(json == null || json.isBlank() ? "[]" : json);
            if (!result.isArray() || result.size() > 100) throw new IllegalArgumentException();
            for (JsonNode node : result) if (!node.isObject()) throw new IllegalArgumentException();
            return result;
        } catch (Exception e) { throw new BusinessException(label + "明細の形式が不正です（100行以内）。"); }
    }
    private static String text(String value, String label, boolean required) {
        String result = value == null ? "" : value.trim();
        if (required && result.isEmpty()) throw new BusinessException(label + "を確認して入力してください。");
        if (result.length() > 255) throw new BusinessException(label + "は255文字以内で入力してください。");
        return result;
    }
    private static Integer number(String value, String label, boolean quantity) {
        if (!quantity && value.isBlank()) return 0;
        try {
            if (!value.matches("[0-9]+")) throw new IllegalArgumentException();
            int n = Integer.parseInt(value);
            if (quantity && n < 1) throw new IllegalArgumentException();
            return n;
        } catch (IllegalArgumentException e) {throw new BusinessException(label + "を" + (quantity ? "1以上" : "0以上") + "の整数で確認・入力してください。");}
    }
}
