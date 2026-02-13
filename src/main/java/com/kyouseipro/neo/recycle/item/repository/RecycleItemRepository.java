package com.kyouseipro.neo.recycle.item.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.recycle.item.entity.RecycleItemEntity;
import com.kyouseipro.neo.recycle.item.mapper.RecycleItemEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecycleItemRepository {
    private final SqlRepository sqlRepository;
    
    /**
     * IDによる取得。
     * @param orderId
     * @return IDから取得したEntityをかえす。
     */
    public RecycleItemEntity findById(int id) {
        String sql = RecycleItemSqlBuilder.buildFindById();

        return sqlRepository.queryOne(
            sql,
            (ps, en) -> {
                int index = 1;
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            RecycleItemEntityMapper::map
        );
    } 

    /**
     * Codeによる取得。
     * @param code
     * @return IDから取得したEntityをかえす。
     */
    public RecycleItemEntity findByCode(int code) {
        String sql = RecycleItemSqlBuilder.buildFindByCode();
        
        return sqlRepository.queryOne(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, code);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            RecycleItemEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(RecycleItemEntity entity, String userName) {
        String sql = "INSERT INTO recycle_items (code, name, version, state) OUTPUT INSERTED.recycle_item_id VALUES (?, ?, ?, ?);";
        try {
            return sqlRepository.insert(
                sql,
                (ps, en) -> {
                    int index = 1;
                    ps.setInt(index++, en.getCode());
                    ps.setString(index++, en.getName());
                    ps.setInt(index++, en.getVersion());
                    ps.setInt(index++, en.getState());
                },
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
    public int update(RecycleItemEntity entity, String userName) {
        String sql = RecycleItemSqlBuilder.buildUpdate();

        try {
            int count = sqlRepository.updateRequired(
                sql,
                (ps, e) -> RecycleItemParameterBinder.bindUpdate(ps, e, userName),
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
     * 削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String userName) {
        String sql = RecycleItemSqlBuilder.buildDeleteByIds(list.getIds().size());

        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> RecycleItemParameterBinder.bindDeleteForIds(ps, e.getIds()),
            list
        );

        return count;
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleItemEntity> downloadCsvByIds(IdListRequest list, String userName) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = RecycleItemSqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        return sqlRepository.queryList(
            sql,
            (ps, e) -> RecycleItemParameterBinder.bindDownloadCsvForIds(ps, e.getIds()),
            RecycleItemEntityMapper::map,
            list
        );
    }
}