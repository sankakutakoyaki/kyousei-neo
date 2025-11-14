package com.kyouseipro.neo.repository.sales;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.sales.OrderListEntity;
import com.kyouseipro.neo.mapper.sales.OrderListEntityMapper;
import com.kyouseipro.neo.query.parameter.sales.OrderListParameterBinder;
import com.kyouseipro.neo.query.sql.sales.OrderListSqlBuilder;
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
        String sql = OrderListSqlBuilder.buildFindAllSql();

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
    public List<OrderListEntity> findByEntityFromBetweenDate(LocalDate start, LocalDate end) {
        String sql = OrderListSqlBuilder.buildFindByBetweenOrderListEntity();

        return sqlRepository.findAll(
            sql,
            ps -> OrderListParameterBinder.bindFindByBetweenOrderListEntity(ps, start, end),
            OrderListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}

