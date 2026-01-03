package com.kyouseipro.neo.repository.order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
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
     * @param id
     * @return IDから取得したEntityをかえす。
     */
    public Optional<OrderItemEntity> findById(int id) {
        String sql = OrderSqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> OrderItemParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? OrderItemEntityMapper.map(rs) : null,
            id
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

        return sqlRepository.findAll(
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

        return sqlRepository.findAll(
            sql,
            (ps, v) -> OrderItemParameterBinder.bindFindByBetween(ps, start, end),
            OrderItemEntityMapper::map // ← ここで ResultSet を map
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
                if (entity.getOrder_item_id() > 0) {
                    sql += OrderItemSqlBuilder.buildUpdate(index++);
                } else {
                    sql += OrderItemSqlBuilder.buildInsert(index++);
                }
            }
        }

        // // return sqlRepository.execute(
        // //     sql,
        // //     (pstmt, emp) -> OrderItemParameterBinder.bindSave(pstmt, itemList, editor),
        // //     rs -> rs.next() ? rs.getInt("order_item_id") : null,
        // //     itemList
        // // );
        // try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> OrderItemParameterBinder.bindSave(ps, list, editor),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("order_item_id");

                    if (rs.next()) {
                        throw new IllegalStateException("ID取得結果が複数行です");
                    }
                    return id;
                },
                list
            );
        // } catch (RuntimeException e) {
        //     if (SqlExceptionUtil.isDuplicateKey(e)) {
        //         throw new BusinessException("このコードはすでに使用されています。");
        //     }
        //     throw e;
        // }
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(List<SimpleData> list, String editor) {
        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = OrderItemSqlBuilder.buildDeleteByIds(ids.size());

        // return sqlRepository.executeUpdate(
        //     sql,
        //     ps -> OrderItemParameterBinder.bindDeleteByIds(ps, orderItemIds, editor)
        // );
        // // return result; // 成功件数。0なら削除なし

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> OrderItemParameterBinder.bindDeleteByIds(ps, ids, editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<OrderItemEntity> downloadCsvByIds(List<SimpleData> list, String editor) {
        // List<Integer> orderIds = Utilities.createSequenceByIds(ids);
        // String sql = OrderItemSqlBuilder.buildDownloadCsvByIds(orderIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> OrderItemParameterBinder.bindDownloadCsvByIds(ps, orderIds),
        //     OrderItemEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = OrderItemSqlBuilder.buildDownloadCsvByIds(ids.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> OrderItemParameterBinder.bindDownloadCsvByIds(ps, ids),
            OrderItemEntityMapper::map
        );
    }
}
