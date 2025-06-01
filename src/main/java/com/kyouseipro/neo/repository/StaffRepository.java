package com.kyouseipro.neo.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.query.parameter.StaffParameterBinder;
import com.kyouseipro.neo.query.sql.StaffSqlBuilder;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StaffRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    // 新規登録
    public Integer insertStaff(StaffEntity staff, String editor) {
        String sql = StaffSqlBuilder.buildInsertStaffSql();

        return sqlRepository.execute(
            sql,
            (pstmt, entity) -> StaffParameterBinder.bindInsertStaffParameters(pstmt, entity, editor),
            rs -> rs.next() ? rs.getInt("staff_id") : null,
            staff
        );
    }

    // 更新
    public Integer updateStaff(StaffEntity staff, String editor) {
        String sql = StaffSqlBuilder.buildUpdateStaffSql();

        return sqlRepository.execute(
            sql,
            (pstmt, entity) -> StaffParameterBinder.bindUpdateStaffParameters(pstmt, entity, editor),
            rs -> rs.next() ? rs.getInt("staff_id") : null,
            staff
        );
    }

    // staff_idによる取得
    public StaffEntity findById(int staffId) {
        return genericRepository.findOne(
        "SELECT * FROM staffs WHERE staff_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, staffId); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            StaffEntity::new // Supplier<T>
        );
    }

    // 全件取得の例（必要に応じて）
    public List<StaffEntity> findAll() {
        return genericRepository.findAll(
            "SELECT s.*, c.name as company_name, o.name as office_name FROM staffs s" + 
            " INNER LEFT OUTER JOIN companies c ON c.company_id = s.company_id" + 
            " INNER LEFT OUTER JOIN offices o ON o.office_id = s.office_id" + 
            " WHERE NOT (c.category = 0) AND NOT (s.state = ?) AND NOT (c.state = ?) AND NOT (o.state = ?)",
            ps -> {
                ps.setInt(1, Enums.state.DELETE.getCode());     // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
                ps.setInt(3, Enums.state.DELETE.getCode());     // 3番目の ?
            },
            StaffEntity::new
        );
    }
}

