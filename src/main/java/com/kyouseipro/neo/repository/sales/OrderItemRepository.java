package com.kyouseipro.neo.repository.sales;

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
    
    public List<OrderItemEntity> findAllByOrderId(int id, String editor) {
        String sql = OrderItemSqlBuilder.buildFindAllByOrderIdSql();

        return sqlRepository.findAll(
            sql,
            ps -> OrderItemParameterBinder.bindFindAllByOrderId(ps, id),
            OrderItemEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
