package com.kyouseipro.neo.common.validation.repository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ValidateRepository {
    private final SqlRepository sqlRepository;

    public String findAbbrNameByCode(int code) {
        String sql =
            "SELECT DISTINCT abbr_name " +
            "FROM recycle_makers " +
            "WHERE code = ? " +
            "  AND state <> 2";

        return sqlRepository.queryOne(
            sql,
            (pstmt, c) -> pstmt.setInt(1, c),
            rs -> rs.getString(1),
            code
        );
    }
}
