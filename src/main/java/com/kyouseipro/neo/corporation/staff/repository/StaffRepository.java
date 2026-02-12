package com.kyouseipro.neo.corporation.staff.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.corporation.staff.entity.StaffEntity;
import com.kyouseipro.neo.corporation.staff.entity.StaffEntityRequest;
import com.kyouseipro.neo.corporation.staff.mapper.StaffEntityMapper;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StaffRepository {
    private final SqlRepository sqlRepository;
   
    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public StaffEntity findById(int id) {
        String sql = StaffSqlBuilder.buildFindById();

        return sqlRepository.queryOne(
            sql,
            (ps, en) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            StaffEntityMapper::map
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<StaffEntity> findAll() {
        String sql = StaffSqlBuilder.buildFindAll();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            StaffEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(StaffEntityRequest entity, String editor) {
        String sql = StaffSqlBuilder.buildBulkInsert(entity);

        return sqlRepository.queryOne(
            sql,
            (ps, en) -> StaffParameterBinder.bindBulkInsert(ps, en, editor),
            rs -> {
                if (!rs.next()) {
                    throw new BusinessException("登録に失敗しました");
                }
                int id = rs.getInt("staff_id");

                if (rs.next()) {
                    throw new IllegalStateException("ID取得結果が複数行です");
                }
                return id;
            },
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(StaffEntityRequest entity, String editor) {
        String sql = StaffSqlBuilder.buildBulkUpdate(entity);

        int count = sqlRepository.update(
            sql,
            (ps, e) -> StaffParameterBinder.bindBulkUpdate(ps, e, editor),
            entity
        );

        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        String sql = StaffSqlBuilder.buildDeleteByIds(list.getIds().size());

        int count = sqlRepository.update(
            sql,
            (ps, e) -> StaffParameterBinder.bindDeleteByIds(ps, e.getIds(), editor),
            list
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
    public List<StaffEntity> downloadCsvByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = StaffSqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        return sqlRepository.queryList(
            sql,
            (ps, e) -> StaffParameterBinder.bindDownloadCsvByIds(ps, e.getIds()),
            StaffEntityMapper::map,
            list
        );
    }
}

