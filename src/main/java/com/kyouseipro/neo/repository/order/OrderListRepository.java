package com.kyouseipro.neo.repository.order;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.order.OrderListEntity;
import com.kyouseipro.neo.mapper.order.OrderListEntityMapper;
import com.kyouseipro.neo.query.parameter.order.OrderListParameterBinder;
import com.kyouseipro.neo.query.sql.order.OrderListSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderListRepository {
    private final SqlRepository sqlRepository;

    /**
     * 全件取得
     * @return
     */
    public List<OrderListEntity> findAll() {
        String sql = OrderListSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> OrderListParameterBinder.bindFindAll(ps, null),
            OrderListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 日付指定で受注情報を取得
     * @param date
     * @return
     */
    public List<OrderListEntity> findByBetween(LocalDate start, LocalDate end) {
        String sql = OrderListSqlBuilder.buildFindByBetween();

        return sqlRepository.findAll(
            sql,
            ps -> OrderListParameterBinder.bindFindByBetween(ps, start, end),
            OrderListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}

