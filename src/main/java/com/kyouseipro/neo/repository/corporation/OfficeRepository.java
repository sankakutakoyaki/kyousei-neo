package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.mapper.corporation.OfficeEntityMapper;
import com.kyouseipro.neo.query.parameter.corporation.OfficeParameterBinder;
import com.kyouseipro.neo.query.sql.corporation.OfficeSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OfficeRepository {
    private final SqlRepository sqlRepository;

    public Integer insert(OfficeEntity office, String editor) {
        String sql = OfficeSqlBuilder.buildInsert();

        return sqlRepository.execute(
            sql,
            (pstmt, off) -> OfficeParameterBinder.bindInsert(pstmt, off, editor),
            rs -> rs.next() ? rs.getInt("office_id") : null,
            office
        );
    }

    public Integer update(OfficeEntity office, String editor) {
        String sql = OfficeSqlBuilder.buildUpdate();

        return sqlRepository.execute(
            sql,
            (pstmt, off) -> OfficeParameterBinder.bindUpdate(pstmt, off, editor),
            rs -> rs.next() ? rs.getInt("office_id") : null,
            office
        );
    }

    public int deleteByIds(List<SimpleData> ids, String editor) {
        List<Integer> officeIds = Utilities.createSequenceByIds(ids);
        String sql = OfficeSqlBuilder.buildDeleteByIds(officeIds.size());

        int result = sqlRepository.executeUpdate(
            sql,
            ps -> OfficeParameterBinder.bindDeleteForIds(ps, officeIds, editor)
        );

        return result; // 成功件数。0なら削除なし
    }

    public List<OfficeEntity> downloadCsvByIds(List<SimpleData> ids, String editor) {
        List<Integer> officeIds = Utilities.createSequenceByIds(ids);
        String sql = OfficeSqlBuilder.buildDownloadCsvByIds(officeIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> OfficeParameterBinder.bindDownloadCsvForIds(ps, officeIds),
            OfficeEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // IDによる取得
    public OfficeEntity findById(int officeId) {
        String sql = OfficeSqlBuilder.buildFindById();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> OfficeParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? OfficeEntityMapper.map(rs) : null,
            officeId
        );
    }

    // 全件取得
    public List<OfficeEntity> findAll() {
        String sql = OfficeSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeParameterBinder.bindFindAll(ps, null),
            OfficeEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<OfficeEntity> findAllClient() {
        String sql = OfficeSqlBuilder.buildFindAllClient();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeParameterBinder.bindFindAllClient(ps, null),
            OfficeEntityMapper::map
        );
    }
}

