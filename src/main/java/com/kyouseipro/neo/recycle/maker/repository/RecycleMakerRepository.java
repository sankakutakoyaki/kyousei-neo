package com.kyouseipro.neo.recycle.maker.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
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
    public RecycleMakerEntity findById(int id) {
        String sql = RecycleMakerSqlBuilder.buildFindById();
        
        return sqlRepository.queryOne(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            RecycleMakerEntityMapper::map
        );
    }

    /**
     * Codeによる取得。
     * @param code
     * @return CODEから取得したEntityを返す。
     */
    public RecycleMakerEntity findByCode(int code) {
        String sql = RecycleMakerSqlBuilder.buildFindByCode();

        return sqlRepository.queryOne(
            sql,
            (ps, en) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, code);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            RecycleMakerEntityMapper::map
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<RecycleMakerEntity> findAll() {
        String sql = RecycleMakerSqlBuilder.buildFindAll();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
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
            return sqlRepository.insert(
                sql,
                (ps, en) -> RecycleMakerParameterBinder.bindInsert(ps, en),
                rs -> rs.getInt("recycle_maker_id"),
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
            int count = sqlRepository.updateRequired(
                sql,
                (ps, e) -> RecycleMakerParameterBinder.bindUpdate(ps, e),
                entity
            );

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
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        String sql = RecycleMakerSqlBuilder.buildDeleteByIds(list.getIds().size());
        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> RecycleMakerParameterBinder.bindDeleteByIds(ps, e.getIds()),
            list
        );

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

        return sqlRepository.queryList(
            sql,
            (ps, e) -> RecycleMakerParameterBinder.bindDownloadCsvByIds(ps, e.getIds()),
            RecycleMakerEntityMapper::map,
            list
        );
    }
}
