package com.kyouseipro.neo.sql.handler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;
    private final SqlRepository sqlRepository;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.ORDER_SAVE;
    }

    @Override
    @Transactional
    public Object execute(
            QueryDefinition def,
            SelectRequest req
    ) {

        Map<String, Object> params = req.getParams();

        String editor =
                (String) params.getOrDefault("editor", "system");

        // =========================
        // 商品明細
        // =========================

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items =
                (List<Map<String, Object>>) params.get("items");

        // =========================
        // 作業明細
        // =========================

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> works =
                (List<Map<String, Object>>) params.get("works");

        // =========================
        // 受注ID判定
        // =========================

        Object orderIdValue = params.get("orderId");

        boolean isNewOrder =
                orderIdValue == null
                || Long.valueOf(orderIdValue.toString()) == 0;

        // Long orderId;
        Long orderId = null;

        // =====================================================
        // 編集の場合
        // 保存前に既存明細IDを取得しておく
        // =====================================================

        List<Long> existingItemIds = List.of();
        List<Long> existingWorkIds = List.of();

        if (!isNewOrder) {

            orderId = Long.valueOf(orderIdValue.toString());

            // 保存前のDB状態を取得
            existingItemIds = findOrderItemIds(orderId);
            existingWorkIds = findOrderWorkIds(orderId);
        }

        // =====================================================
        // 削除対象を「保存前の状態」で確定
        // =====================================================

        List<Long> deleteItemIds = List.of();
        List<Long> deleteWorkIds = List.of();

        if (!isNewOrder) {

            // -------------------------
            // 商品
            // -------------------------

            if (items != null) {

                List<Long> requestItemIds =
                        items.stream()
                                .map(item -> item.get("orderItemId"))
                                .filter(id -> id != null)
                                .map(id -> Long.valueOf(id.toString()))
                                .toList();

                deleteItemIds =
                        existingItemIds.stream()
                                .filter(id -> !requestItemIds.contains(id))
                                .toList();
            }

            // -------------------------
            // 作業
            // -------------------------

            if (works != null) {

                List<Long> requestWorkIds =
                        works.stream()
                                .map(work -> work.get("orderWorkId"))
                                .filter(id -> id != null)
                                .map(id -> Long.valueOf(id.toString()))
                                .toList();

                deleteWorkIds =
                        existingWorkIds.stream()
                                .filter(id -> !requestWorkIds.contains(id))
                                .toList();
            }
        }

        // =====================================================
        // 受注
        // =====================================================

        Map<String, Object> orderParams =
                new LinkedHashMap<>(params);

        // 明細はordersには入れない
        orderParams.remove("items");
        orderParams.remove("works");

        // 商品入力欄はordersには入れない
        for (String field : ORDER_ITEM_INPUT_FIELDS) {
            orderParams.remove(field);
        }

        // 作業入力欄はordersには入れない
        for (String field : ORDER_WORK_INPUT_FIELDS) {
            orderParams.remove(field);
        }

        if (isNewOrder) {

            // =========================
            // 新規受注
            // =========================

            orderId = baseRepository.insert(
                    def.getTableMeta(),
                    orderParams,
                    editor
            );

        } else {

            // =========================
            // 既存受注
            // =========================

            baseRepository.update(
                    def.getTableMeta(),
                    orderParams,
                    editor
            );
        }

        // =====================================================
        // 商品明細 INSERT / UPDATE
        // =====================================================

        if (items != null) {

            for (Map<String, Object> item : items) {

                Map<String, Object> itemParams =
                        new LinkedHashMap<>(item);

                itemParams.put("orderId", orderId);

                // フロント専用
                itemParams.remove("_tempId");

                Object itemIdValue =
                        itemParams.get("orderItemId");

                boolean isNewItem =
                        itemIdValue == null
                        || Long.valueOf(itemIdValue.toString()) == 0;

                if (isNewItem) {

                    // 商品新規
                    baseRepository.insert(
                            Tables.ORDER_ITEM_BY_IDS,
                            itemParams,
                            editor
                    );

                } else {

                    // 商品更新
                    baseRepository.update(
                            Tables.ORDER_ITEM_BY_IDS,
                            itemParams,
                            editor
                    );
                }
            }
        }

        // =====================================================
        // 作業明細 INSERT / UPDATE
        // =====================================================

        if (works != null) {

            for (Map<String, Object> work : works) {

                Map<String, Object> workParams =
                        new LinkedHashMap<>(work);

                workParams.put("orderId", orderId);

                // フロント専用
                workParams.remove("_tempId");

                Object workIdValue =
                        workParams.get("orderWorkId");

                boolean isNewWork =
                        workIdValue == null
                        || Long.valueOf(workIdValue.toString()) == 0;

                if (isNewWork) {

                    // 作業新規
                    baseRepository.insert(
                            Tables.ORDER_WORK_BY_IDS,
                            workParams,
                            editor
                    );

                } else {

                    // 作業更新
                    baseRepository.update(
                            Tables.ORDER_WORK_BY_IDS,
                            workParams,
                            editor
                    );
                }
            }
        }

        // =====================================================
        // 商品削除
        // =====================================================

        if (!deleteItemIds.isEmpty()) {
            baseRepository.deleteByIds(
                    Tables.ORDER_ITEM_BY_IDS,
                    deleteItemIds,
                    editor
            );
        }

        // =====================================================
        // 作業削除
        // =====================================================

        if (!deleteWorkIds.isEmpty()) {
            baseRepository.deleteByIds(
                    Tables.ORDER_WORK_BY_IDS,
                    deleteWorkIds,
                    editor
            );
        }

        // =====================================================
        // 結果
        // =====================================================

        return Map.of(
                "data", orderId,
                "count", 1
        );
    }

    // =========================================================
    // 商品ID取得
    // =========================================================

    private List<Long> findOrderItemIds(Long orderId) {

        String sql = """
            SELECT order_item_id
            FROM order_items
            WHERE order_id = ?
            AND state = ?
            """;

        return sqlRepository.queryList(
                sql,
                (ps, id) -> {
                    ps.setLong(1, id);
                    ps.setInt(2, State.INITIAL.getCode());
                },
                rs -> rs.getLong("order_item_id"),
                orderId
        );
    }

    // =========================================================
    // 作業ID取得
    // =========================================================

    private List<Long> findOrderWorkIds(Long orderId) {

        String sql = """
            SELECT order_work_id
            FROM order_works
            WHERE order_id = ?
            AND state = ?
            """;

        return sqlRepository.queryList(
                sql,
                (ps, id) -> {
                    ps.setLong(1, id);
                    ps.setInt(2, State.INITIAL.getCode());
                },
                rs -> rs.getLong("order_work_id"),
                orderId
        );
    }

    // =========================================================
    // 受注から除外する商品入力項目
    // =========================================================

    private static final List<String> ORDER_ITEM_INPUT_FIELDS =
            List.of(
                    "janCode",
                    "itemName",
                    "itemMaker",
                    "itemModel",
                    "itemQuantity"
            );

    // =========================================================
    // 受注から除外する作業入力項目
    // =========================================================

    private static final List<String> ORDER_WORK_INPUT_FIELDS =
            List.of(
                    "orderWorkCode",
                    "orderWorkName",
                    "orderWorkPrice",
                    "orderWorkQuantity"
            );
}