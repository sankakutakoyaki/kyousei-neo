package com.kyouseipro.neo.repository.sales;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.sales.DeliveryStaffEntity;
import com.kyouseipro.neo.mapper.sales.DeliveryStaffEntityMapper;
import com.kyouseipro.neo.query.parameter.sales.DeliveryStaffParameterBinder;
import com.kyouseipro.neo.query.sql.sales.DeliveryStaffSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DeliveryStaffRepository {
    private final SqlRepository sqlRepository;
    
    public List<DeliveryStaffEntity> findAllByOrderId(int id, String editor) {
        String sql = DeliveryStaffSqlBuilder.buildFindAllByOrderIdSql();

        return sqlRepository.findAll(
            sql,
            ps -> DeliveryStaffParameterBinder.bindFindAllByOrderId(ps, id),
            DeliveryStaffEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
