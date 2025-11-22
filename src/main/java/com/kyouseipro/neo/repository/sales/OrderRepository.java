package com.kyouseipro.neo.repository.sales;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.sales.OrderEntity;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.mapper.sales.OrderEntityMapper;
import com.kyouseipro.neo.query.parameter.sales.OrderParameterBinder;
import com.kyouseipro.neo.query.sql.sales.OrderItemSqlBuilder;
import com.kyouseipro.neo.query.sql.sales.OrderSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final SqlRepository sqlRepository;

    /**
     * 新規作成。
     * @param orderEntity
     * @param itemEntity
     * @param editor
     * @return 新規IDを返す。
     */
    public Integer insertOrder(OrderEntity orderEntity, List<OrderItemEntity> itemEntityList, String editor) {
        String sql = OrderSqlBuilder.buildInsertOrderSql();
        int index = 1;
        for(int i = 0; i < itemEntityList.size(); i++) {
            // sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
            if (orderEntity.getOrder_id() > 0) {
                sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
            } else {
                sql += OrderItemSqlBuilder.buildInsertNewOrderItemSql(index++);
            }
        }
        orderEntity.setItem_list(itemEntityList);

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> OrderParameterBinder.bindInsertOrderParameters(pstmt, orderEntity, editor),
            rs -> rs.next() ? rs.getInt("order_id") : null,
            orderEntity
        );
    }

    /**
     * 更新。
     * @param order
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer updateOrder(OrderEntity orderEntity, List<OrderItemEntity> itemEntityList, String editor) {
        String sql = OrderSqlBuilder.buildUpdateOrderSql();
        // String sql = "";
        int index = 1;
        for (OrderItemEntity orderItemEntity : itemEntityList) {
            if (orderItemEntity.getState() == Enums.state.DELETE.getCode()) {
                sql += OrderItemSqlBuilder.buildDeleteOrderItemSql(index++);
            } else {
                if (orderItemEntity.getOrder_item_id() > 0) {
                    sql += OrderItemSqlBuilder.buildUpdateOrderItemSql(index++);
                } else {
                    sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
                }
            }
        }
        orderEntity.setItem_list(itemEntityList);

        Integer result = sqlRepository.executeUpdate(
            sql,
            pstmt -> OrderParameterBinder.bindUpdateOrderParameters(pstmt, orderEntity, editor)
        );

        return result; // 成功件数。0なら更新なし
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

        Integer result = sqlRepository.executeUpdate(
            sql,
            ps -> OrderParameterBinder.bindDeleteForIds(ps, orderIds, editor)
        );

        return result; // 成功件数。0なら削除なし
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
    public OrderEntity findById(int orderId) {
        String sql = OrderSqlBuilder.buildFindByIdSql();

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
    public List<OrderEntity> findAll() {
        String sql = OrderSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> OrderParameterBinder.bindFindAll(ps, null),
            OrderEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
