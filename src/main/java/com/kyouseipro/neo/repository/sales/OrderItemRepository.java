package com.kyouseipro.neo.repository.sales;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.mapper.sales.OrderItemEntityMapper;
import com.kyouseipro.neo.query.parameter.sales.OrderItemParameterBinder;
import com.kyouseipro.neo.query.sql.sales.OrderItemSqlBuilder;
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
    public OrderItemEntity findById(String sql, int orderItemId) {
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
    public Integer saveOrderItemList(String sql, List<OrderItemEntity> itemList, String editor) {

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
    public Integer deleteOrderItemByIds(String sql, List<Integer> ids, String editor) {

        return sqlRepository.executeUpdate(
            sql,
            ps -> OrderItemParameterBinder.bindDeleteForIds(ps, ids, editor)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<OrderItemEntity> downloadCsvOrderItemByIds(String sql, List<Integer> ids, String editor) {

        return sqlRepository.findAll(
            sql,
            ps -> OrderItemParameterBinder.bindDownloadCsvForIds(ps, ids),
            OrderItemEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
