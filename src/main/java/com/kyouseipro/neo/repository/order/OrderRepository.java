package com.kyouseipro.neo.repository.order;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
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
    public Integer save(OrderEntity entity,
                         List<OrderItemEntity> items,
                         List<DeliveryStaffEntity> staffs,
                         List<WorkContentEntity> works,
                         String editor) {

        String sql = buildSaveOrderSql(entity, items, staffs, works);
        entity.setItem_list(items);
        entity.setStaff_list(staffs);
        entity.setWork_list(works);
        return sqlRepository.executeUpdate(sql, ps -> {
            if (entity.getOrder_id() > 0) {
                OrderParameterBinder.bindUpdate(ps, entity, editor);
            } else {
                OrderParameterBinder.bindInsert(ps, entity, editor);
            }
        });
    }

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

    private String resolveItemSql(OrderItemEntity e, boolean isUpdate, int index) {
        if (e.getState() == Enums.state.DELETE.getCode()) return OrderItemSqlBuilder.buildDelete(index);
        if (isUpdate && e.getOrder_item_id() > 0) return OrderItemSqlBuilder.buildUpdate(index);
        if (isUpdate) return OrderItemSqlBuilder.buildInsert(index);
        return OrderItemSqlBuilder.buildInsertByNewOrder(index);
    }

    private String resolveWorkSql(WorkContentEntity e, boolean isUpdate, int index) {
        if (e.getState() == Enums.state.DELETE.getCode()) return WorkContentSqlBuilder.buildDelete(index);
        if (isUpdate && e.getWork_content_id() > 0) return WorkContentSqlBuilder.buildUpdate(index);
        if (isUpdate) return WorkContentSqlBuilder.buildInsert(index);
        return WorkContentSqlBuilder.buildInsertByNewOrder(index);
    }

    private String resolveStaffSql(DeliveryStaffEntity e, boolean isUpdate, int index) {
        if (e.getState() == Enums.state.DELETE.getCode()) return DeliveryStaffSqlBuilder.buildDelete(index);
        if (isUpdate && e.getDelivery_staff_id() > 0) return DeliveryStaffSqlBuilder.buildUpdate(index);
        if (isUpdate) return DeliveryStaffSqlBuilder.buildInsert(index);
        return DeliveryStaffSqlBuilder.buildInsertByNewOrder(index);
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteByIds(List<SimpleData> ids, String editor) {
        List<Integer> orderIds = Utilities.createSequenceByIds(ids);
        String sql = OrderSqlBuilder.buildDeleteByIds(orderIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> OrderParameterBinder.bindDeleteByIds(ps, orderIds, editor)
        );

        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<OrderEntity> downloadCsvByIds(List<SimpleData> ids, String editor) {
        List<Integer> orderIds = Utilities.createSequenceByIds(ids);
        String sql = OrderSqlBuilder.buildDownloadCsvByIds(orderIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> OrderParameterBinder.bindDownloadCsvByIds(ps, orderIds),
            OrderEntityMapper::map // ← ここで ResultSet を map
        );
    }
   
    /**
     * IDによる取得。
     * @param orderId
     * @return IDから取得したEntityをかえす。
     */
    public OrderEntity findById(String sql, int orderId) {
        // String sql = OrderSqlBuilder.buildFindById();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> OrderParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? OrderEntityMapper.map(rs) : null,
            orderId
        );
    }

    /**
     * 全件取得。
     * @return 全てのEntityを返す。
     */
    public List<OrderEntity> findAll(String sql) {
        // String sql = OrderSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> OrderParameterBinder.bindFindAll(ps, null),
            OrderEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
