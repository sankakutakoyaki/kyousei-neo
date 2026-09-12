package com.kyouseipro.neo.domain.business.order.arrival;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.provider.Tables;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemArrivalService {
    private final SqlRepository sql;
    private final BaseSqlRepository base;

    private String editor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "system" : auth.getName();
    }
    private Map<String,Object> lock(long id) {
        var rows = sql.selectMap("SELECT * FROM order_items WITH (UPDLOCK, HOLDLOCK) WHERE order_item_id = ? AND state = 0", List.of(id));
        if (rows.isEmpty()) throw new BusinessException("商品が存在しないか削除されています。");
        return rows.get(0);
    }
    private List<Map<String,Object>> entries(long id) {
        return sql.selectMap("SELECT * FROM order_item_arrivals WHERE order_item_id = ? ORDER BY arrival_date, arrival_id", List.of(id));
    }
    private int received(long id) {
        return entries(id).stream().filter(r -> r.get("cancelledAt") == null)
                .mapToInt(r -> ((Number)r.get("quantity")).intValue()).sum();
    }
    public static int quantity(Object value) {
        try {
            int n = Integer.parseInt(String.valueOf(value));
            if (n > 0) return n;
        } catch (RuntimeException ignored) { }
        throw new BusinessException("数量は1以上の整数を入力してください。");
    }
    private static Date date(Object value, boolean optional) {
        if (value == null || value.toString().isBlank()) {
            if (optional) return null;
            throw new BusinessException("入荷日を入力してください。");
        }
        try { return Date.valueOf(LocalDate.parse(value.toString())); }
        catch (RuntimeException e) { throw new BusinessException("日付を正しく入力してください。"); }
    }
    public static void validateVersion(Map<String,Object> item, Object version) {
        if (version == null || !String.valueOf(item.get("version")).equals(version.toString()))
            throw new BusinessException("商品が更新されています。開き直して確認してください。");
    }
    public static void validateTotal(int ordered, int received) {
        if (received > ordered) throw new BusinessException("入荷数が商品数量を超えます。数量を確認してください。");
    }
    @Transactional
    public Map<String,Object> detail(long id) {
        var item = lock(id);
        return Map.of("item", item, "entries", entries(id));
    }
    @Transactional
    public void record(Map<String,Object> p, String operation) {
        long id = Long.parseLong(p.get("orderItemId").toString());
        var item = lock(id);
        String request;
        try { request = UUID.fromString(String.valueOf(p.get("requestId"))).toString(); }
        catch (RuntimeException e) { throw new BusinessException("画面を開き直してください。"); }
        // 同じ要求の再送は、最初に確定した結果をそのまま利用する。
        var duplicate = sql.selectMap("SELECT order_item_id FROM order_item_arrivals WHERE request_id = ? OR cancel_request_id = ?", List.of(request, request));
        if (!duplicate.isEmpty()) {
            if (((Number)duplicate.get(0).get("orderItemId")).longValue() != id) throw new BusinessException("登録要求が重複しています。");
            return;
        }
        validateVersion(item, p.get("version"));
        int total = received(id);
        Long replaces = null;
        if (!operation.equals("orderItemArrival")) {
            replaces = Long.valueOf(p.get("arrivalId").toString());
            final long target = replaces;
            var old = entries(id).stream().filter(r -> ((Number)r.get("arrivalId")).longValue() == target && r.get("cancelledAt") == null)
                    .findFirst().orElseThrow(() -> new BusinessException("この履歴は訂正・取消済みです。開き直してください。"));
            total -= ((Number)old.get("quantity")).intValue();
        }
        boolean cancel = operation.equals("orderItemArrivalCancel");
        int amount = cancel ? 0 : quantity(p.get("quantity"));
        Date day = cancel ? null : date(p.get("arrivalDate"), false);
        if (day != null && day.toLocalDate().isAfter(LocalDate.now())) throw new BusinessException("未来の日付は入荷予定日に入力してください。");
        if ((long)total + amount > quantity(item.get("itemQuantity")))
            throw new BusinessException("入荷数が商品数量を超えます。数量を確認してください。");
        if (replaces != null) sql.update("UPDATE order_item_arrivals SET cancelled_at = GETDATE(), cancelled_by = ?, cancel_request_id = ? WHERE arrival_id = ? AND order_item_id = ? AND cancelled_at IS NULL", List.of(editor(), request, replaces, id));
        if (!cancel) sql.update("INSERT order_item_arrivals(order_item_id, arrival_date, quantity, request_id, replaces_id, registered_by) VALUES (?, ?, ?, ?, ?, ?)", Arrays.asList(id, day, amount, request, replaces, editor()));
        recompute(item, total + amount);
    }
    private void recompute(Map<String,Object> item, int total) {
        Object day = total == quantity(item.get("itemQuantity"))
                ? entries(((Number)item.get("orderItemId")).longValue()).stream().filter(r -> r.get("cancelledAt") == null)
                    .map(r -> r.get("arrivalDate").toString()).max(String::compareTo).map(Date::valueOf).orElse(null)
                : null;
        var update = new LinkedHashMap<String,Object>();
        update.put("orderItemId", item.get("orderItemId"));
        update.put("version", item.get("version"));
        update.put("arrivalDate", day);
        base.update(Tables.ORDER_ITEM_BY_IDS, update, editor());
    }
    @Transactional
    public long save(Map<String,Object> params) {
        long id = params.get("orderItemId") == null ? 0 : Long.parseLong(params.get("orderItemId").toString());
        var data = new LinkedHashMap<String,Object>();
        for (String key : List.of("janCode", "itemName", "itemMaker", "itemModel", "itemQuantity", "itemPayment", "remarks", "expectedArrivalDate"))
            if (params.containsKey(key)) data.put(key, params.get(key));
        if (data.containsKey("expectedArrivalDate")) data.put("expectedArrivalDate", date(data.get("expectedArrivalDate"), true));
        if (id == 0) {
            data.put("itemQuantity", quantity(data.getOrDefault("itemQuantity", 1)));
            data.put("orderId", 0); data.put("state", 0); data.put("arrivalDate", null);
            return base.insert(Tables.ORDER_ITEM_BY_IDS, data, editor());
        }
        var item = lock(id);
        validateVersion(item, params.get("version"));
        int ordered = quantity(data.getOrDefault("itemQuantity", item.get("itemQuantity")));
        int total = received(id);
        validateTotal(ordered, total);
        data.put("orderItemId", id); data.put("version", item.get("version"));
        // 履歴から算出する入荷日をクライアントから上書きさせない。
        data.put("arrivalDate", total == ordered ? entries(id).stream().filter(r -> r.get("cancelledAt") == null)
            .map(r -> r.get("arrivalDate").toString()).max(String::compareTo).map(Date::valueOf).orElse(null) : null);
        base.update(Tables.ORDER_ITEM_BY_IDS, data, editor());
        return id;
    }
}
