package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.mapper.corporation.StaffEntityMapper;
import com.kyouseipro.neo.query.parameter.corporation.StaffParameterBinder;
import com.kyouseipro.neo.query.sql.corporation.StaffSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StaffRepository {
    private final SqlRepository sqlRepository;

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


    public int deleteStaffByIds(List<SimpleData> ids, String editor) {
        List<Integer> staffIds = Utilities.createSequenceByIds(ids);
        String sql = StaffSqlBuilder.buildDeleteStaffForIdsSql(staffIds.size());

        int result = sqlRepository.executeUpdate(
            sql,
            ps -> StaffParameterBinder.bindDeleteForIds(ps, staffIds)
        );

        return result; // 成功件数。0なら削除なし
    }

    public List<StaffEntity> downloadCsvStaffByIds(List<SimpleData> ids, String editor) {
        List<Integer> staffIds = Utilities.createSequenceByIds(ids);
        String sql = StaffSqlBuilder.buildDownloadCsvStaffForIdsSql(staffIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> StaffParameterBinder.bindFindAll(ps, null),
            StaffEntityMapper::map // ← ここで ResultSet を map
        );
    }
   
    // IDによる取得
    public StaffEntity findById(int staffId) {
        String sql = StaffSqlBuilder.buildFindByIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> StaffParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? StaffEntityMapper.map(rs) : null,
            staffId
        );
    }

    // 全件取得
    public List<StaffEntity> findAll() {
        String sql = StaffSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> StaffParameterBinder.bindFindAll(ps, null),
            StaffEntityMapper::map // ← ここで ResultSet を map
        );
    }
}

