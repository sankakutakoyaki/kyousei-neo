package com.kyouseipro.neo.repository.work;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.work.WorkPriceEntity;
import com.kyouseipro.neo.mapper.work.WorkPriceEntityMapper;
import com.kyouseipro.neo.query.parameter.work.WorkPriceParameterBinder;
import com.kyouseipro.neo.query.sql.work.WorkPriceSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkPriceRepository {
    private final SqlRepository sqlRepository;

    public WorkPriceEntity findById(int id) {
        String sql = WorkPriceSqlBuilder.buildFindById();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> WorkPriceParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? WorkPriceEntityMapper.map(rs) : null,
            id
        );
    }

    public List<WorkPriceEntity> findAllByCompanyId(int id) {
        String sql = WorkPriceSqlBuilder.buildFindAllByCompanyId();

        return sqlRepository.findAll(
            sql,
            ps -> WorkPriceParameterBinder.bindFindAllByCompanyId(ps, id),
            WorkPriceEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public Integer insert(WorkPriceEntity entity, String editor) {
        int index = 1;
        String sql = WorkPriceSqlBuilder.buildInsert(index);
        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> WorkPriceParameterBinder.bindInsert(pstmt, entity, editor, index),
            rs -> rs.next() ? rs.getInt("work_price_id") : null,
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public Integer update(WorkPriceEntity entity, String editor) {
        int index = 1;
        String sql = WorkPriceSqlBuilder.buildUpdate(index);

        Integer result = sqlRepository.executeUpdate(
            sql,
            pstmt -> WorkPriceParameterBinder.bindUpdate(pstmt, entity, editor, index)
        );

        return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<WorkPriceEntity> downloadCsvByIds(List<SimpleData> ids) {
        List<Integer> workPriceIds = Utilities.createSequenceByIds(ids);
        String sql = WorkPriceSqlBuilder.buildDownloadCsvByIds(workPriceIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> WorkPriceParameterBinder.bindDownloadCsvForIds(ps, workPriceIds),
            WorkPriceEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
