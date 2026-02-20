package com.kyouseipro.neo.common.file.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.file.entity.ConstructionFileGroupEntity;
import com.kyouseipro.neo.common.file.mappaer.ConstructionFileGroupMapper;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConstructionFileGroupRepository {

    private final SqlRepository sqlRepository;

    /**
     * 施工単位でグループ一覧取得
     */
    public List<ConstructionFileGroupEntity> findByConstructionId(Long constructionId) {

        return sqlRepository.queryList(
            """
            SELECT *
            FROM construction_file_group
            WHERE construction_id = ?
            ORDER BY display_order, group_id
            """,
            (ps, v) -> ps.setLong(1, constructionId),
            ConstructionFileGroupMapper::map
        );
    }

    /**
     * グループ登録（ID取得）
     */
    public Long insert(ConstructionFileGroupEntity group) {

        return sqlRepository.queryOne(
            """
            INSERT INTO construction_file_group
            (construction_id, group_name, work_date,
             display_order, note, create_date, update_date)
            OUTPUT INSERTED.group_id
            VALUES (?, ?, ?, ?, ?, SYSDATETIME(), SYSDATETIME())
            """,
            (ps, v) -> {
                int i = 1;
                ps.setLong(i++, group.getConstructionId());
                ps.setString(i++, group.getGroupName());
                if (group.getWorkDate() != null) {
                    ps.setDate(i++, java.sql.Date.valueOf(group.getWorkDate()));
                } else {
                    ps.setNull(i++, java.sql.Types.DATE);
                }
                ps.setInt(i++, group.getDisplayOrder());
                ps.setString(i++, group.getNote());
            },
            rs -> rs.getLong(1)
        );
    }

    /**
     * グループ更新
     */
    public void update(ConstructionFileGroupEntity group) {

        sqlRepository.update(
            """
            UPDATE construction_file_group
            SET group_name = ?,
                work_date = ?,
                display_order = ?,
                note = ?,
                update_date = SYSDATETIME()
            WHERE group_id = ?
            """,
            (ps, v) -> {
                int i = 1;
                ps.setString(i++, group.getGroupName());

                if (group.getWorkDate() != null) {
                    ps.setDate(i++, java.sql.Date.valueOf(group.getWorkDate()));
                } else {
                    ps.setNull(i++, java.sql.Types.DATE);
                }

                ps.setInt(i++, group.getDisplayOrder());
                ps.setString(i++, group.getNote());
                ps.setLong(i++, group.getGroupId());
            }
        );
    }

    /**
     * グループ削除
     */
    public void delete(Long groupId) {

        sqlRepository.update(
            "DELETE FROM construction_file_group WHERE group_id = ?",
            (ps, v) -> ps.setLong(1, groupId)
        );
    }
}