package com.kyouseipro.neo.repository.common;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ValidateRepository {
    private final SqlRepository sqlRepository;

    public Optional<String> findAbbrNameByCode(int code) {
        String sql =
            "SELECT DISTINCT abbr_name " +
            "FROM recycle_makers " +
            "WHERE code = ? " +
            "  AND state <> 2";

        return sqlRepository.executeRequired(
            sql,
            (pstmt, c) -> pstmt.setInt(1, c),
            rs -> rs.next() ? Optional.ofNullable(rs.getString(1)) : Optional.empty(),
            code
        );
    }
}
