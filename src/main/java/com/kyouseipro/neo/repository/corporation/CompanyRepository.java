package com.kyouseipro.neo.repository.corporation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.mapper.corporation.CompanyEntityMapper;
import com.kyouseipro.neo.query.parameter.corporation.CompanyParameterBinder;
import com.kyouseipro.neo.query.sql.corporation.CompanySqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {
    private final SqlRepository sqlRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<CompanyEntity> findById(int id) {
        String sql = CompanySqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> CompanyParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? CompanyEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyEntity> findAll() {
        String sql = CompanySqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyParameterBinder.bindFindAll(ps, null),
            CompanyEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 荷主全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyEntity> findAllClient() {
        String sql = CompanySqlBuilder.buildFindAllClient();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyParameterBinder.bindFindAllClient(ps, null),
            CompanyEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(CompanyEntity entity, String editor) {
        String sql = CompanySqlBuilder.buildInsert();

        // // return sqlRepository.execute(
        // //     sql,
        // //     (pstmt, comp) -> CompanyParameterBinder.bindInsert(pstmt, comp, editor),
        // //     rs -> rs.next() ? rs.getInt("company_id") : null,
        // //     company
        // // );
        // try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> CompanyParameterBinder.bindInsert(ps, en, editor),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("company_id");

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
    public int update(CompanyEntity entity, String editor) {
        String sql = CompanySqlBuilder.buildUpdate();

        // // return sqlRepository.execute(
        // //     sql,
        // //     (pstmt, comp) -> CompanyParameterBinder.bindUpdate(pstmt, comp, editor),
        // //     rs -> rs.next() ? rs.getInt("company_id") : null,
        // //     company
        // // );
        // try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> CompanyParameterBinder.bindUpdate(ps, entity, editor)
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
        String sql = CompanySqlBuilder.buildDeleteByIds(ids.size());

        // int result = sqlRepository.executeUpdate(
        //     sql,
        //     ps -> CompanyParameterBinder.bindDeleteByIds(ps, companyIds, editor)
        // );

        // return result; // 成功件数。0なら削除なし
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> CompanyParameterBinder.bindDeleteByIds(ps, ids, editor)
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
    public List<CompanyEntity> downloadCsvByIds(List<SimpleData> list, String editor) {
        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = CompanySqlBuilder.buildDownloadCsvByIds(ids.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> CompanyParameterBinder.bindDownloadCsvByIds(ps, companyIds),
        //     CompanyEntityMapper::map // ← ここで ResultSet を map
        // );
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyParameterBinder.bindDownloadCsvByIds(ps, ids),
            CompanyEntityMapper::map
        );
    }
}
