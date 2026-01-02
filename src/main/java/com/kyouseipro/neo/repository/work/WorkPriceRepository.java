package com.kyouseipro.neo.repository.work;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
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

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<WorkPriceEntity> findById(int id) {
        String sql = WorkPriceSqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> WorkPriceParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? WorkPriceEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 全件取得（CompanyIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkPriceEntity> findAllByCompanyId(int id) {
        String sql = WorkPriceSqlBuilder.buildFindAllByCompanyId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkPriceParameterBinder.bindFindAllByCompanyId(ps, id),
            WorkPriceEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(WorkPriceEntity entity, String editor) {
        String sql = WorkPriceSqlBuilder.buildInsert(1);
        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, emp) -> WorkPriceParameterBinder.bindInsert(pstmt, entity, editor, index),
        //     rs -> rs.next() ? rs.getInt("work_price_id") : null,
        //     entity
        // );
        try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> WorkPriceParameterBinder.bindInsert(ps, entity, editor, 1),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("work_price_id");

                    if (rs.next()) {
                        throw new IllegalStateException("ID取得結果が複数行です");
                    }
                    return id;
                },
                entity
            );
        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(WorkPriceEntity entity, String editor) {
        String sql = WorkPriceSqlBuilder.buildUpdate(1);

        // Integer result = sqlRepository.executeUpdate(
        //     sql,
        //     pstmt -> WorkPriceParameterBinder.bindUpdate(pstmt, entity, editor, index)
        // );

        // return result; // 成功件数。0なら削除なし
        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> WorkPriceParameterBinder.bindUpdate(ps, entity, editor, 1)
            );

            if (count == 0) {
                throw new BusinessException("更新対象が存在しません");
            }

            return count;

        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<WorkPriceEntity> downloadCsvByIds(List<SimpleData> list) {
        // List<Integer> workPriceIds = Utilities.createSequenceByIds(ids);
        // String sql = WorkPriceSqlBuilder.buildDownloadCsvByIds(workPriceIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> WorkPriceParameterBinder.bindDownloadCsvForIds(ps, workPriceIds),
        //     WorkPriceEntityMapper::map // ← ここで ResultSet を map
        // );

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = WorkPriceSqlBuilder.buildDownloadCsvByIds(ids.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkPriceParameterBinder.bindDownloadCsvForIds(ps, ids),
            WorkPriceEntityMapper::map
        );
    }
}
