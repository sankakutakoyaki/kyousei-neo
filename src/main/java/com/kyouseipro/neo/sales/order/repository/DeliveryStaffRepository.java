package com.kyouseipro.neo.sales.order.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.sales.order.entity.DeliveryStaffEntity;
import com.kyouseipro.neo.sales.order.mapper.DeliveryStaffEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DeliveryStaffRepository {
    private final SqlRepository sqlRepository;

    /**
     * 全件取得（OrderIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<DeliveryStaffEntity> findAllByOrderId(int id, String editor) {
        String sql = DeliveryStaffSqlBuilder.buildFindAllByOrderId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> DeliveryStaffParameterBinder.bindFindAllByOrderId(ps, id),
            DeliveryStaffEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
