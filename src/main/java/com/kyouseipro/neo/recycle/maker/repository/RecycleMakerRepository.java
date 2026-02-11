package com.kyouseipro.neo.recycle.maker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.recycle.maker.entity.RecycleMakerEntity;
import com.kyouseipro.neo.recycle.maker.mapper.RecycleMakerEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecycleMakerRepository {
    private final SqlRepository sqlRepository;
    
    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<RecycleMakerEntity> findById(int id) {
        String sql = RecycleMakerSqlBuilder.buildFindById();
        
        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> RecycleMakerParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? RecycleMakerEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * Codeによる取得。
     * @param code
     * @return CODEから取得したEntityを返す。
     */
    public Optional<RecycleMakerEntity> findByCode(int code) {
        String sql = RecycleMakerSqlBuilder.buildFindByCode();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> RecycleMakerParameterBinder.bindFindByCode(ps, en),
            rs -> rs.next() ? RecycleMakerEntityMapper.map(rs) : null,
            code
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<RecycleMakerEntity> findAll() {
        String sql = RecycleMakerSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> RecycleMakerParameterBinder.bindFindAll(ps, null),
            RecycleMakerEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(RecycleMakerEntity entity) {
        String sql = RecycleMakerSqlBuilder.buildInsert();

        try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> RecycleMakerParameterBinder.bindInsert(ps, en),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("recycle_maker_id");

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
    public int update(RecycleMakerEntity entity) {
        String sql = RecycleMakerSqlBuilder.buildUpdate();

        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> RecycleMakerParameterBinder.bindUpdate(ps, entity)
            );

            if (count == 0) {
                throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
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
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String userName) {
        String sql = RecycleMakerSqlBuilder.buildDeleteByIds(list.getIds().size());

        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> RecycleMakerParameterBinder.bindDeleteByIds(ps, list.getIds())
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return listで選択したEntityリストを返す。
     */
    public List<RecycleMakerEntity> downloadCsvByIds(IdListRequest list, String userName) {

        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = RecycleMakerSqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> RecycleMakerParameterBinder.bindDownloadCsvByIds(ps, list.getIds()),
            RecycleMakerEntityMapper::map
        );
    }
}
