package com.kyouseipro.neo.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.order.OrderEntity;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.entity.order.WorkContentEntity;
import com.kyouseipro.neo.mapper.order.OrderEntityMapper;
import com.kyouseipro.neo.query.parameter.order.OrderParameterBinder;
import com.kyouseipro.neo.query.sql.order.DeliveryStaffSqlBuilder;
import com.kyouseipro.neo.query.sql.order.OrderItemSqlBuilder;
import com.kyouseipro.neo.query.sql.order.OrderSqlBuilder;
import com.kyouseipro.neo.query.sql.order.WorkContentSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

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
    public Optional<OrderEntity> findById(int id) {
        String sql = OrderSqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> OrderParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? OrderEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 全件取得。
     * @return 全てのEntityを返す。
     */
    public List<OrderEntity> findAll() {
        String sql = OrderSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> OrderParameterBinder.bindFindAll(ps, null),
            OrderEntityMapper::map // ← ここで ResultSet を map
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
        entity.setItem_list(items);
        entity.setStaff_list(staffs);
        entity.setWork_list(works);
    }

    // insert/update 判定
    private boolean isUpdate(OrderEntity entity) {
        return entity.getOrder_id() > 0;
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

        // String sql = buildSaveOrderSql(entity, items, staffs, works);
        // entity.setItem_list(items);
        // entity.setStaff_list(staffs);
        // entity.setWork_list(works);
        // return sqlRepository.executeUpdate(sql, ps -> {
        //     if (entity.getOrder_id() > 0) {
        //         OrderParameterBinder.bindUpdate(ps, entity, editor);
        //     } else {
        //         OrderParameterBinder.bindInsert(ps, entity, editor);
        //     }
        // });
        boolean update = isUpdate(entity);
        String sql = buildSaveOrderSql(entity, items, staffs, works);
        
        attachChildren(entity, items, staffs, works);
        // String sql = update
        //         ? buildUpdateOrderSql(entity)
        //         : buildInsertOrderSql(entity);

        return sqlRepository.executeUpdate(sql, ps -> {
            if (update) {
                OrderParameterBinder.bindUpdate(ps, entity, editor);
            } else {
                OrderParameterBinder.bindInsert(ps, entity, editor);
            }
        });
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

        if (entity.getOrder_id() > 0) {
            sql.append(OrderSqlBuilder.buildUpdate());
        } else {
            sql.append(OrderSqlBuilder.buildInsert());
        }

        // 商品
        for (OrderItemEntity item : items) {
            sql.append(resolveItemSql(item, entity.getOrder_id() > 0, index++));
        }

        // 作業
        for (WorkContentEntity work : works) {
            sql.append(resolveWorkSql(work, entity.getOrder_id() > 0, index++));
        }

        // 配送スタッフ
        for (DeliveryStaffEntity staff : staffs) {
            sql.append(resolveStaffSql(staff, entity.getOrder_id() > 0, index++));
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
        if (isUpdate && e.getOrder_item_id() > 0) return OrderItemSqlBuilder.buildUpdate(index);
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
        if (isUpdate && e.getWork_content_id() > 0) return WorkContentSqlBuilder.buildUpdate(index);
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
        if (isUpdate && e.getDelivery_staff_id() > 0) return DeliveryStaffSqlBuilder.buildUpdate(index);
        if (isUpdate) return DeliveryStaffSqlBuilder.buildInsert(index);
        return DeliveryStaffSqlBuilder.buildInsertByNewOrder(index);
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(List<SimpleData> list, String editor) {
        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = OrderSqlBuilder.buildDeleteByIds(ids.size());

        // return sqlRepository.executeUpdate(
        //     sql,
        //     ps -> OrderParameterBinder.bindDeleteByIds(ps, orderIds, editor)
        // );
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> OrderParameterBinder.bindDeleteByIds(ps, ids, editor)
        );
        if (count == 0) {
            throw new BusinessException("削除対象が存在しません");
        }

        return count;
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<OrderEntity> downloadCsvByIds(List<SimpleData> list, String editor) {
        // List<Integer> orderIds = Utilities.createSequenceByIds(ids);
        // String sql = OrderSqlBuilder.buildDownloadCsvByIds(orderIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> OrderParameterBinder.bindDownloadCsvByIds(ps, ids),
        //     OrderEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = OrderSqlBuilder.buildDownloadCsvByIds(ids.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> OrderParameterBinder.bindDownloadCsvByIds(ps, ids),
            OrderEntityMapper::map
        );
    }
}
