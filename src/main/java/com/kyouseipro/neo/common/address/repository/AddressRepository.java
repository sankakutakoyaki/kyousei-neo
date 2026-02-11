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

    // IDによる取得
    public AddressEntity findByPostalCode(String code) {
        String sql = "SELECT * FROM address WHERE NOT (state = ?) AND postal_code = ?";
        return sqlRepository.query(
            sql,
            ps -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setString(index++, code);
            },
            rs -> rs.next() ? AddressEntityMapper.map(rs) : null
        );
    // public Optional<AddressEntity> findByPostalCode(String code) {
    //     return sqlRepository.executeQuery(
    //         sql,
    //         (ps, p) -> {
    //             int index = 1;
    //             ps.setInt(index++, Enums.state.DELETE.getCode());
    //             ps.setString(index++, code);
    //         },
    //         rs -> rs.next() ? AddressEntityMapper.map(rs) : null,
    //         code
    //     );
    }
}
