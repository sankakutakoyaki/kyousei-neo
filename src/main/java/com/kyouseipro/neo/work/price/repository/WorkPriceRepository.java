package com.kyouseipro.neo.work.price.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.work.price.entity.WorkPriceEntity;
import com.kyouseipro.neo.work.price.mapper.WorkPriceEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkPriceRepository {
    private final SqlRepository sqlRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public WorkPriceEntity findById(int id) {
        String sql = WorkPriceSqlBuilder.buildFindById();

        return sqlRepository.queryOne(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            WorkPriceEntityMapper::map
        );
    }

    /**
     * 全件取得（CompanyIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkPriceEntity> findAllByCompanyId(int id) {
        String sql = WorkPriceSqlBuilder.buildFindAllByCompanyId();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, id);
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            WorkPriceEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(WorkPriceEntity entity, String editor) {
        String sql = WorkPriceSqlBuilder.buildInsert(1);

        return sqlRepository.insert(
            sql,
            (ps, e) -> WorkPriceParameterBinder.bindInsert(ps, e, editor, 1),
            rs -> rs.getInt("work_price_id"),
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(WorkPriceEntity entity, String editor) {
        String sql = WorkPriceSqlBuilder.buildUpdate(1);

        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> WorkPriceParameterBinder.bindUpdate(ps, e, editor, 1),
            entity
        );

        return count;
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<WorkPriceEntity> downloadCsvByIds(IdListRequest list, String userName) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = WorkPriceSqlBuilder.buildDownloadCsvByIds(list.getIds().size());
        return sqlRepository.queryList(
            sql,
            (ps, e) -> WorkPriceParameterBinder.bindDownloadCsvForIds(ps, e.getIds()),
            WorkPriceEntityMapper::map,
            list
        );
    }
}
