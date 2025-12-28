package com.kyouseipro.neo.repository.order;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;
import com.kyouseipro.neo.mapper.order.DeliveryStaffEntityMapper;
import com.kyouseipro.neo.query.parameter.order.DeliveryStaffParameterBinder;
import com.kyouseipro.neo.query.sql.order.DeliveryStaffSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DeliveryStaffRepository {
    private final SqlRepository sqlRepository;
    
    public List<DeliveryStaffEntity> findAllByOrderId(int id, String editor) {
        String sql = DeliveryStaffSqlBuilder.buildFindAllByOrderId();

        return sqlRepository.findAll(
            sql,
            ps -> DeliveryStaffParameterBinder.bindFindAllByOrderId(ps, id),
            DeliveryStaffEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
