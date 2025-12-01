package com.kyouseipro.neo.repository.sales;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.sales.OrderEntity;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.entity.sales.OrderListEntity;
import com.kyouseipro.neo.mapper.data.SimpleDataMapper;
import com.kyouseipro.neo.mapper.sales.OrderEntityMapper;
import com.kyouseipro.neo.mapper.sales.OrderItemEntityMapper;
import com.kyouseipro.neo.mapper.sales.OrderListEntityMapper;
import com.kyouseipro.neo.query.parameter.qualification.QualificationsParameterBinder;
import com.kyouseipro.neo.query.parameter.sales.OrderItemParameterBinder;
import com.kyouseipro.neo.query.parameter.sales.OrderListParameterBinder;
import com.kyouseipro.neo.query.parameter.sales.OrderParameterBinder;
import com.kyouseipro.neo.query.sql.qualification.QualificationsSqlBuilder;
import com.kyouseipro.neo.query.sql.sales.OrderItemSqlBuilder;
import com.kyouseipro.neo.query.sql.sales.OrderListSqlBuilder;
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
}
