package com.kyouseipro.neo.repository.common;

import java.util.Optional;

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
    public Optional<AddressEntity> findByPostalCode(String code) {
        String sql = AddressSqlBuilder.buildFindByPostalCode();

        return sqlRepository.executeQuery(
            sql,
            (pstmt, en) -> AddressParameterBinder.bindFindByPostalCode(pstmt, en),
            rs -> rs.next() ? AddressEntityMapper.map(rs) : null,
            code
        );
    }
}
