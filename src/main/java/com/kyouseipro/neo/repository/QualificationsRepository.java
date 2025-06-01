package com.kyouseipro.neo.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.common.QualificationsEntity;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.query.parameter.QualificationsParameterBinder;
import com.kyouseipro.neo.query.sql.QualificationsSqlBuilder;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QualificationsRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    // INSERT
    public Integer insertQualifications(QualificationsEntity q, String editor) {
        String sql = QualificationsSqlBuilder.buildInsertQualificationsSql();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationsParameterBinder.bindInsertQualificationsParameters(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_id") : null,
            q
        );
    }

    // UPDATE
    public Integer updateQualifications(QualificationsEntity q, String editor) {
        String sql = QualificationsSqlBuilder.buildUpdateQualificationsSql();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationsParameterBinder.bindUpdateQualificationsParameters(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_id") : null,
            q
        );
    }

    // IDによる検索
    public QualificationsEntity findById(int qualificationId) {
        return genericRepository.findOne(
        "SELECT * FROM qualifications WHERE qualifiations_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, qualificationId); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            QualificationsEntity::new // Supplier<T>
        );
    }

    // 全件取得
    public List<QualificationsEntity> findAll() {
        return genericRepository.findAll(
            "SELECT * FROM qualifications WHERE NOT (state = ?)",
            ps -> ps.setInt(1, Enums.state.DELETE.getCode()),
            QualificationsEntity::new
        );
    }
}

