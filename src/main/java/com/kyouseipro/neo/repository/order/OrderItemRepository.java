package com.kyouseipro.neo.repository.order;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.mapper.order.OrderItemEntityMapper;
import com.kyouseipro.neo.query.parameter.order.OrderItemParameterBinder;
import com.kyouseipro.neo.query.sql.order.OrderItemSqlBuilder;
import com.kyouseipro.neo.query.sql.order.OrderSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {
    private final SqlRepository sqlRepository;
    
    /**
     * IDによる取得。
     * @param orderId
     * @return IDから取得したEntityをかえす。
     */
    public OrderItemEntity findById(int orderItemId) {
        String sql = OrderSqlBuilder.buildFindByIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> OrderItemParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? OrderItemEntityMapper.map(rs) : null,
            orderItemId
        );
    }

    /**
     * 受注IDでアイテムを抽出
     * @param id
     * @param editor
     * @return
     */
    public List<OrderItemEntity> findAllByOrderId(int id, String editor) {
        String sql = OrderItemSqlBuilder.buildFindAllByOrderIdSql();

        return sqlRepository.findAll(
            sql,
            ps -> OrderItemParameterBinder.bindFindAllByOrderId(ps, id),
            OrderItemEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 期間内のアイテムリストを取得
     * @param start
     * @param end
     * @return
     */
    public List<OrderItemEntity> findByEntityFromBetweenDate(LocalDate start, LocalDate end) {
        String sql = OrderItemSqlBuilder.buildFindByBetweenOrderItemEntity();

        return sqlRepository.findAll(
            sql,
            ps -> OrderItemParameterBinder.bindFindByBetweenOrderItemEntity(ps, start, end),
            OrderItemEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param itemEntity
     * @param editor
     * @return 新規IDを返す。
     */
    public Integer saveOrderItemList(List<OrderItemEntity> itemList, String editor) {
        String sql = "";
        int index = 1;

        for (OrderItemEntity entity : itemList) {
            if (entity.getState() == Enums.state.DELETE.getCode()) {
                sql += OrderItemSqlBuilder.buildDeleteOrderItemSql(index++);
            } else {
                if (entity.getOrder_item_id() > 0) {
                    sql += OrderItemSqlBuilder.buildUpdateOrderItemSql(index++);
                } else {
                    sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
                }
            }
        }

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> OrderItemParameterBinder.bindSaveOrderItemListParameters(pstmt, itemList, editor),
            rs -> rs.next() ? rs.getInt("order_item_id") : null,
            itemList
        );
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteOrderItemByIds(List<SimpleData> ids, String editor) {
        List<Integer> orderItemIds = Utilities.createSequenceByIds(ids);
        String sql = OrderItemSqlBuilder.buildDeleteOrderItemForIdsSql(orderItemIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> OrderItemParameterBinder.bindDeleteForIds(ps, orderItemIds, editor)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<OrderItemEntity> downloadCsvOrderItemByIds(List<SimpleData> ids, String editor) {
        List<Integer> orderIds = Utilities.createSequenceByIds(ids);
        String sql = OrderItemSqlBuilder.buildDownloadCsvOrderItemForIdsSql(orderIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> OrderItemParameterBinder.bindDownloadCsvForIds(ps, orderIds),
            OrderItemEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
