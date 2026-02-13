package com.kyouseipro.neo.sales.order.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.sales.order.entity.DeliveryStaffEntity;
import com.kyouseipro.neo.sales.order.entity.OrderEntity;
import com.kyouseipro.neo.sales.order.entity.OrderItemEntity;
import com.kyouseipro.neo.sales.order.entity.WorkContentEntity;
import com.kyouseipro.neo.sales.order.mapper.OrderEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final SqlRepository sqlRepository;

    /**
     * IDによる取得。
     * @param orderId
     * @return IDから取得したEntityをかえす。
     */
    public OrderEntity findById(int id) {
        String sql = OrderSqlBuilder.buildFindById();

        return sqlRepository.queryOne(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            OrderEntityMapper::map
        );
    }

    /**
     * 全件取得。
     * @return 全てのEntityを返す。
     */
    public List<OrderEntity> findAll() {
        String sql = OrderSqlBuilder.buildFindAll();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            OrderEntityMapper::map
        );
    }

    /**
     * Entityへの詰め替え
     * @param entity
     * @param items
     * @param staffs
     * @param works
     */
    private void attachChildren(
            OrderEntity entity,
            List<OrderItemEntity> items,
            List<DeliveryStaffEntity> staffs,
            List<WorkContentEntity> works
    ) {
        entity.setItemList(items);
        entity.setStaffList(staffs);
        entity.setWorkList(works);
    }

    // insert/update 判定
    private boolean isUpdate(OrderEntity entity) {
        return entity.getOrderId() > 0;
    }

    /**
     * 新規・更新処理
     * @param entity
     * @param items
     * @param staffs
     * @param works
     * @param editor
     * @return
     */
    public int save(OrderEntity entity,
                         List<OrderItemEntity> items,
                         List<DeliveryStaffEntity> staffs,
                         List<WorkContentEntity> works,
                         String editor) {

        boolean update = isUpdate(entity);
        String sql = buildSaveOrderSql(entity, items, staffs, works);
        
        attachChildren(entity, items, staffs, works);

        return sqlRepository.update(
            sql,
            (ps, e) -> {
                if (update) {
                    OrderParameterBinder.bindUpdate(ps, e, editor);
                } else {
                    OrderParameterBinder.bindInsert(ps, e, editor);
                }
            },
            entity
        );
    }

    /**
     * 各種SQL文を作成
     * @param entity
     * @param items
     * @param staffs
     * @param works
     * @return
     */
    private String buildSaveOrderSql(OrderEntity entity,
                                    List<OrderItemEntity> items,
                                    List<DeliveryStaffEntity> staffs,
                                    List<WorkContentEntity> works) {

        StringBuilder sql = new StringBuilder();
        int index = 1;

        if (entity.getOrderId() > 0) {
            sql.append(OrderSqlBuilder.buildUpdate());
        } else {
            sql.append(OrderSqlBuilder.buildInsert());
        }

        // 商品
        for (OrderItemEntity item : items) {
            sql.append(resolveItemSql(item, entity.getOrderId() > 0, index++));
        }

        // 作業
        for (WorkContentEntity work : works) {
            sql.append(resolveWorkSql(work, entity.getOrderId() > 0, index++));
        }

        // 配送スタッフ
        for (DeliveryStaffEntity staff : staffs) {
            sql.append(resolveStaffSql(staff, entity.getOrderId() > 0, index++));
        }

        return sql.toString();
    }

    /**
     * SQL文を条件に応じて組みたる
     * @param e
     * @param isUpdate
     * @param index
     * @return
     */
    private String resolveItemSql(OrderItemEntity e, boolean isUpdate, int index) {
        if (e.getState() == Enums.state.DELETE.getCode()) return OrderItemSqlBuilder.buildDelete(index);
        if (isUpdate && e.getOrderItemId() > 0) return OrderItemSqlBuilder.buildUpdate(index);
        if (isUpdate) return OrderItemSqlBuilder.buildInsert(index);
        return OrderItemSqlBuilder.buildInsertByNewOrder(index);
    }

    /**
     * SQL文を条件に応じて組みたる
     * @param e
     * @param isUpdate
     * @param index
     * @return
     */
    private String resolveWorkSql(WorkContentEntity e, boolean isUpdate, int index) {
        if (e.getState() == Enums.state.DELETE.getCode()) return WorkContentSqlBuilder.buildDelete(index);
        if (isUpdate && e.getWorkContentId() > 0) return WorkContentSqlBuilder.buildUpdate(index);
        if (isUpdate) return WorkContentSqlBuilder.buildInsert(index);
        return WorkContentSqlBuilder.buildInsertByNewOrder(index);
    }

    /**
     * SQL文を条件に応じて組みたる
     * @param e
     * @param isUpdate
     * @param index
     * @return
     */
    private String resolveStaffSql(DeliveryStaffEntity e, boolean isUpdate, int index) {
        if (e.getState() == Enums.state.DELETE.getCode()) return DeliveryStaffSqlBuilder.buildDelete(index);
        if (isUpdate && e.getDeliveryStaffId() > 0) return DeliveryStaffSqlBuilder.buildUpdate(index);
        if (isUpdate) return DeliveryStaffSqlBuilder.buildInsert(index);
        return DeliveryStaffSqlBuilder.buildInsertByNewOrder(index);
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }
        
        String sql = OrderSqlBuilder.buildDeleteByIds(list.getIds().size());
        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> OrderParameterBinder.bindDeleteByIds(ps, e.getIds(), editor),
            list
        );

        return count;
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<OrderEntity> downloadCsvByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = OrderSqlBuilder.buildDownloadCsvByIds(list.getIds().size());
        return sqlRepository.queryList(
            sql,
            (ps, e) -> OrderParameterBinder.bindDownloadCsvByIds(ps, e.getIds()),
            OrderEntityMapper::map,
            list
        );
    }
}
