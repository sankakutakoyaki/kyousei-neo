package com.kyouseipro.neo.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.query.parameter.OfficeParameterBinder;
import com.kyouseipro.neo.query.sql.OfficeSqlBuilder;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OfficeRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    public Integer insertOffice(OfficeEntity office, String editor) {
        String sql = OfficeSqlBuilder.buildInsertOfficeSql();

        return sqlRepository.execute(
            sql,
            (pstmt, off) -> OfficeParameterBinder.bindInsertOfficeParameters(pstmt, off, editor),
            rs -> rs.next() ? rs.getInt("office_id") : null,
            office
        );
    }

    public Integer updateOffice(OfficeEntity office, String editor) {
        String sql = OfficeSqlBuilder.buildUpdateOfficeSql();

        return sqlRepository.execute(
            sql,
            (pstmt, off) -> OfficeParameterBinder.bindUpdateOfficeParameters(pstmt, off, editor),
            rs -> rs.next() ? rs.getInt("office_id") : null,
            office
        );
    }

    // office_idによる取得
    public OfficeEntity findById(int officeId) {
        return genericRepository.findOne(
        "SELECT * FROM offices WHERE office_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, officeId); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            OfficeEntity::new // Supplier<T>
        );
    }

    // 全件取得の例（必要に応じて）
    public List<OfficeEntity> findAll() {
        return genericRepository.findAll(
            "SELECT * FROM offices WHERE NOT (state = ?)",
            ps -> ps.setInt(1, Enums.state.DELETE.getCode()),
            OfficeEntity::new
        );
    }

    // 全件取得の例（必要に応じて）
    public List<OfficeEntity> findAllClient() {
        return genericRepository.findAll(
            "SELECT o.*, c.name as company_name, c.name_kana as company_name_kana FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id" + 
            " WHERE NOT (c.category = ?) AND NOT (c.state = ?) AND NOT (o.state = ?)",
            ps -> {
                ps.setInt(1, 0); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
                ps.setInt(3, Enums.state.DELETE.getCode());     // 3番目の ?
            },
            OfficeEntity::new
        );
    }
}

