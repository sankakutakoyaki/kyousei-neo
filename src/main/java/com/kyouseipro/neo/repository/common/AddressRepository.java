package com.kyouseipro.neo.repository.common;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.common.AddressEntity;
import com.kyouseipro.neo.mapper.common.AddressEntityMapper;
import com.kyouseipro.neo.query.parameter.common.AddressParameterBinder;
import com.kyouseipro.neo.query.sql.common.AddressSqlBuilder;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AddressRepository {
    private final SqlRepository sqlRepository;

    // IDによる取得
    public AddressEntity findByPostalCode(String postalCode) {
        String sql = AddressSqlBuilder.buildFindByPostalCode();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> AddressParameterBinder.bindFindByPostalCode(pstmt, comp),
            rs -> rs.next() ? AddressEntityMapper.map(rs) : null,
            postalCode
        );
    }
}
