package com.kyouseipro.neo.repository.corporation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
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

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<OfficeEntity> findById(int id) {
        String sql = OfficeSqlBuilder.buildFindById();

        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, comp) -> OfficeParameterBinder.bindFindById(pstmt, comp),
        //     rs -> rs.next() ? OfficeEntityMapper.map(rs) : null,
        //     officeId
        // );
        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> OfficeParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? OfficeEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<OfficeEntity> findAll() {
        String sql = OfficeSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> OfficeParameterBinder.bindFindAll(ps, null),
            OfficeEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 全件取得（荷主）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<OfficeEntity> findAllClient() {
        String sql = OfficeSqlBuilder.buildFindAllClient();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> OfficeParameterBinder.bindFindAllClient(ps, null),
            OfficeEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(OfficeEntity entity, String editor) {
        String sql = OfficeSqlBuilder.buildInsert();

        // // return sqlRepository.executeRequired(
        // //     sql,
        // //     (pstmt, e) -> OfficeParameterBinder.bindInsert(pstmt, e, editor),
        // //     rs -> rs.next() ? rs.getInt("office_id") : null,
        // //     entity
        // // );
        // try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> OfficeParameterBinder.bindInsert(ps, en, editor),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("office_id");

                    if (rs.next()) {
                        throw new IllegalStateException("ID取得結果が複数行です");
                    }
                    return id;
                },
                entity
            );
        // } catch (RuntimeException e) {
        //     if (SqlExceptionUtil.isDuplicateKey(e)) {
        //         throw new BusinessException("このコードはすでに使用されています。");
        //     }
        //     throw e;
        // }
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(OfficeEntity entity, String editor) {
        String sql = OfficeSqlBuilder.buildUpdate();

        // // return sqlRepository.execute(
        // //     sql,
        // //     (pstmt, off) -> OfficeParameterBinder.bindUpdate(pstmt, off, editor),
        // //     rs -> rs.next() ? rs.getInt("office_id") : null,
        // //     office
        // // );
        // try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> OfficeParameterBinder.bindUpdate(ps, entity, editor)
            );

            if (count == 0) {
                throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
            }

            return count;

        // } catch (RuntimeException e) {
        //     if (SqlExceptionUtil.isDuplicateKey(e)) {
        //         throw new BusinessException("このコードはすでに使用されています。");
        //     }
        //     throw e;
        // }
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(List<SimpleData> list, String editor) {
        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = OfficeSqlBuilder.buildDeleteByIds(ids.size());

        // int result = sqlRepository.executeUpdate(
        //     sql,
        //     ps -> OfficeParameterBinder.bindDeleteForIds(ps, officeIds, editor)
        // );

        // return result; // 成功件数。0なら削除なし
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> OfficeParameterBinder.bindDeleteByIds(ps, ids, editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<OfficeEntity> downloadCsvByIds(List<SimpleData> list, String editor) {
        // List<Integer> officeIds = Utilities.createSequenceByIds(ids);
        // String sql = OfficeSqlBuilder.buildDownloadCsvByIds(officeIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> OfficeParameterBinder.bindDownloadCsvForIds(ps, officeIds),
        //     OfficeEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = OfficeSqlBuilder.buildDownloadCsvByIds(ids.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> OfficeParameterBinder.bindDownloadCsvByIds(ps, ids),
            OfficeEntityMapper::map
        );
    }
}

