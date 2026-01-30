package com.kyouseipro.neo.sales.order.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.sales.order.entity.OrderListEntity;
import com.kyouseipro.neo.sales.order.mapper.OrderListEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderListRepository {
    private final SqlRepository sqlRepository;

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<OrderListEntity> findAll() {
        String sql = OrderListSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> OrderListParameterBinder.bindFindAll(ps, null),
            OrderListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 日付指定で受注情報を取得
     * @param start
     * @param end
     * @return 取得したリストを返す
     */
    public List<OrderListEntity> findByBetween(LocalDate start, LocalDate end) {
        String sql = OrderListSqlBuilder.buildFindByBetween();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> OrderListParameterBinder.bindFindByBetween(ps, start, end),
            OrderListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}

