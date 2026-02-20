package com.kyouseipro.neo.common.address.repository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.address.entity.AddressEntity;
import com.kyouseipro.neo.common.address.mapper.AddressEntityMapper;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AddressRepository {
    private final SqlRepository sqlRepository;

    // PostalCodeによる取得
    public AddressEntity findByPostalCode(String code) {
        
        return sqlRepository.queryOneOrNull(
            "SELECT * FROM address WHERE state <> ? AND postal_code = ?",
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setString(index++, code);
            },
            AddressEntityMapper::map
        );
    }
}
