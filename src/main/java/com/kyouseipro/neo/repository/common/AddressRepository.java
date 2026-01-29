package com.kyouseipro.neo.repository.common;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.common.AddressEntity;
import com.kyouseipro.neo.mapper.common.AddressEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AddressRepository {
    private final SqlRepository sqlRepository;

    // IDによる取得
    public Optional<AddressEntity> findByPostalCode(String code) {
        String sql = "SELECT * FROM address WHERE NOT (state = ?) AND postal_code = ?";

        return sqlRepository.executeQuery(
            sql,
            (ps, p) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setString(index++, code);
            },
            rs -> rs.next() ? AddressEntityMapper.map(rs) : null,
            code
        );
    }
}
