package com.kyouseipro.neo.sales.order.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.sales.order.entity.OrderItemEntity;
import com.kyouseipro.neo.sales.order.mapper.OrderItemEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {
    private final SqlRepository sqlRepository;
    
    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityをかえす。
     */
    public OrderItemEntity findById(int id) {
        String sql = OrderSqlBuilder.buildFindById();

        return sqlRepository.queryOne(
            sql,
            (ps, v) -> OrderItemParameterBinder.bindFindById(ps, id),
            OrderItemEntityMapper::map
        );
    }

    /**
     * 受注IDでアイテムを抽出
     * @param id
     * @param editor
     * @return
     */
    public List<OrderItemEntity> findAllByOrderId(int id, String editor) {
        String sql = OrderItemSqlBuilder.buildFindAllByOrderId();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> OrderItemParameterBinder.bindFindAllByOrderId(ps, id),
            OrderItemEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 期間内のアイテムリストを取得
     * @param start
     * @param end
     * @return
     */
    public List<OrderItemEntity> findByBetweenDate(LocalDate start, LocalDate end) {
        String sql = OrderItemSqlBuilder.buildFindByBetween();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> OrderItemParameterBinder.bindFindByBetween(ps, start, end),
            OrderItemEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param list
     * @param editor
     * @return 新規IDを返す。
     */
    public int save(List<OrderItemEntity> list, String editor) {
        String sql = "";
        int index = 1;

        for (OrderItemEntity entity : list) {
            if (entity.getState() == Enums.state.DELETE.getCode()) {
                sql += OrderItemSqlBuilder.buildDelete(index++);
            } else {
                if (entity.getOrderItemId() > 0) {
                    sql += OrderItemSqlBuilder.buildUpdate(index++);
                } else {
                    sql += OrderItemSqlBuilder.buildInsert(index++);
                }
            }
        }

        return sqlRepository.insert(
        sql,
        (ps, e) -> OrderItemParameterBinder.bindSave(ps, e, editor),
        rs -> rs.getInt("order_item_id"),
        list
    );
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

        String sql = OrderItemSqlBuilder.buildDeleteByIds(list.getIds().size());
        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> OrderItemParameterBinder.bindDeleteByIds(ps, e.getIds(), editor),
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
    public List<OrderItemEntity> downloadCsvByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = OrderItemSqlBuilder.buildDownloadCsvByIds(list.getIds().size());
        return sqlRepository.queryList(
            sql,
            (ps, e) -> OrderItemParameterBinder.bindDownloadCsvByIds(ps, e.getIds()),
            OrderItemEntityMapper::map,
            list
        );
    }
}
