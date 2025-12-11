package com.kyouseipro.neo.repository.sales;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.sales.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.sales.OrderEntity;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.entity.sales.WorkContentEntity;
import com.kyouseipro.neo.mapper.sales.OrderEntityMapper;
import com.kyouseipro.neo.query.parameter.sales.OrderParameterBinder;
import com.kyouseipro.neo.query.sql.sales.DeliveryStaffSqlBuilder;
import com.kyouseipro.neo.query.sql.sales.OrderItemSqlBuilder;
import com.kyouseipro.neo.query.sql.sales.OrderSqlBuilder;
import com.kyouseipro.neo.query.sql.sales.WorkContentSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final SqlRepository sqlRepository;

    // /**
    //  * 新規作成。
    //  * @param orderEntity
    //  * @param itemEntity
    //  * @param editor
    //  * @return 新規IDを返す。
    //  */
    // public Integer insertOrder(String sql, OrderEntity orderEntity, String editor) {
    // // public Integer insertOrder(OrderEntity orderEntity, List<OrderItemEntity> itemEntityList, String editor) {
    //     // String sql = OrderSqlBuilder.buildInsertOrderSql();
    //     // int index = 1;
    //     // for(int i = 0; i < itemEntityList.size(); i++) {
    //     //     // sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
    //     //     if (orderEntity.getOrder_id() > 0) {
    //     //         sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
    //     //     } else {
    //     //         sql += OrderItemSqlBuilder.buildInsertNewOrderItemSql(index++);
    //     //     }
    //     // }
    //     // orderEntity.setItem_list(itemEntityList);

    //     return sqlRepository.execute(
    //         sql,
    //         (pstmt, emp) -> OrderParameterBinder.bindInsertOrderParameters(pstmt, orderEntity, editor),
    //         rs -> rs.next() ? rs.getInt("order_id") : null,
    //         orderEntity
    //     );
    // }

    // /**
    //  * 更新。
    //  * @param order
    //  * @param editor
    //  * @return 成功件数を返す。
    //  */
    // public Integer updateOrder(String sql, OrderEntity orderEntity, String editor) {
    // // public Integer updateOrder(OrderEntity orderEntity, List<OrderItemEntity> itemEntityList, String editor) {
    //     // String sql = OrderSqlBuilder.buildUpdateOrderSql();
    //     // // String sql = "";
    //     // int index = 1;
    //     // for (OrderItemEntity orderItemEntity : itemEntityList) {
    //     //     if (orderItemEntity.getState() == Enums.state.DELETE.getCode()) {
    //     //         sql += OrderItemSqlBuilder.buildDeleteOrderItemSql(index++);
    //     //     } else {
    //     //         if (orderItemEntity.getOrder_item_id() > 0) {
    //     //             sql += OrderItemSqlBuilder.buildUpdateOrderItemSql(index++);
    //     //         } else {
    //     //             sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
    //     //         }
    //     //     }
    //     // }
    //     // orderEntity.setItem_list(itemEntityList);

    //     Integer result = sqlRepository.executeUpdate(
    //         sql,
    //         pstmt -> OrderParameterBinder.bindUpdateOrderParameters(pstmt, orderEntity, editor)
    //     );

    //     return result; // 成功件数。0なら更新なし
    // }

    public Integer saveOrder(OrderEntity entity,
                         List<OrderItemEntity> items,
                         List<DeliveryStaffEntity> staffs,
                         List<WorkContentEntity> works,
                         String editor) {

        String sql = buildSaveOrderSql(entity, items, staffs, works);
        // return sqlRepository.executeUpdate(sql, ps -> 
        //         OrderParameterBinder.bindSaveOrder(ps, entity, items, staffs, works, editor)
        // );
        return sqlRepository.executeUpdate(sql, ps -> {
            if (entity.getOrder_id() > 0) {
                OrderParameterBinder.bindUpdateOrderParameters(ps, entity, editor);
            } else {
                OrderParameterBinder.bindInsertOrderParameters(ps, entity, editor);
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
            sql.append(OrderSqlBuilder.buildUpdateOrderSql());
        } else {
            sql.append(OrderSqlBuilder.buildInsertOrderSql());
        }

        // 商品
        for (OrderItemEntity item : items) {
            sql.append(resolveItemSql(item, entity.getOrder_id() > 0, index));
        }

        // 作業
        for (WorkContentEntity work : works) {
            sql.append(resolveWorkSql(work, entity.getOrder_id() > 0, index));
        }

        // 配送スタッフ
        for (DeliveryStaffEntity staff : staffs) {
            sql.append(resolveStaffSql(staff, entity.getOrder_id() > 0, index));
        }

        return sql.toString();
    }

    private String resolveItemSql(OrderItemEntity e, boolean isUpdate, int index) {
        if (e.getState() == Enums.state.DELETE.getCode()) return OrderItemSqlBuilder.buildDeleteOrderItemSql(index++);
        if (isUpdate && e.getOrder_item_id() > 0) return OrderItemSqlBuilder.buildUpdateOrderItemSql(index++);
        if (isUpdate) return OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
        return OrderItemSqlBuilder.buildInsertOrderItemByNewOrderSql(index++);
    }

    private String resolveWorkSql(WorkContentEntity e, boolean isUpdate, int index) {
        if (e.getState() == Enums.state.DELETE.getCode()) return WorkContentSqlBuilder.buildDeleteWorkContentSql(index++);
        if (isUpdate && e.getWork_content_id() > 0) return WorkContentSqlBuilder.buildUpdateWorkContentSql(index++);
        if (isUpdate) return WorkContentSqlBuilder.buildInsertWorkContentSql(index++);
        return WorkContentSqlBuilder.buildInsertWorkContentByNewOrderSql(index++);
    }

    private String resolveStaffSql(DeliveryStaffEntity e, boolean isUpdate, int index) {
        if (e.getState() == Enums.state.DELETE.getCode()) return DeliveryStaffSqlBuilder.buildDeleteDeliveryStaffSql(index++);
        if (isUpdate && e.getDelivery_staff_id() > 0) return DeliveryStaffSqlBuilder.buildUpdateDeliveryStaffSql(index++);
        if (isUpdate) return DeliveryStaffSqlBuilder.buildInsertDeliveryStaffSql(index++);
        return DeliveryStaffSqlBuilder.buildInsertDeliveryStaffByNewOrderSql(index++);
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteOrderByIds(List<SimpleData> ids, String editor) {
        List<Integer> orderIds = Utilities.createSequenceByIds(ids);
        String sql = OrderSqlBuilder.buildDeleteOrderForIdsSql(orderIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> OrderParameterBinder.bindDeleteForIds(ps, orderIds, editor)
        );

        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<OrderEntity> downloadCsvOrderByIds(List<SimpleData> ids, String editor) {
        List<Integer> orderIds = Utilities.createSequenceByIds(ids);
        String sql = OrderSqlBuilder.buildDownloadCsvOrderForIdsSql(orderIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> OrderParameterBinder.bindDownloadCsvForIds(ps, orderIds),
            OrderEntityMapper::map // ← ここで ResultSet を map
        );
    }
   
    /**
     * IDによる取得。
     * @param orderId
     * @return IDから取得したEntityをかえす。
     */
    public OrderEntity findById(String sql, int orderId) {
        // String sql = OrderSqlBuilder.buildFindByIdSql();

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
        // String sql = OrderSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> OrderParameterBinder.bindFindAll(ps, null),
            OrderEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
