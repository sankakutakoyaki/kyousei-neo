package com.kyouseipro.neo.common.file.repository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.file.entity.FileGroupEntity;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FileGroupRepository {
    private final SqlRepository sqlRepository;

    public Long insert(FileGroupEntity gruoup) {

        String sql = """
            INSERT INTO files_group (
                parent_type,
                parent_id,
                group_name,
                display_order
            )
            OUTPUT INSERTED.group_id
            VALUES (?, ?, ?, 0)
        """;

        return sqlRepository.insert(
            sql,
            (ps, g) -> {
                int idx = 1;
                ps.setString(idx++, g.getParentType());
                ps.setLong(idx++, g.getParentId());
                ps.setString(idx++, g.getGroupName());
            },
            rs -> rs.getLong(1),
            gruoup
        );
    }

    public boolean exists(Long groupId) {

        String sql = """
            SELECT COUNT(*)
            FROM files_group
            WHERE group_id = ?
        """;

        Integer count = sqlRepository.queryOne(
            sql,
            (ps, id) -> ps.setLong(1, id),
            rs -> rs.getInt(1),
            groupId
        );

        return count != null && count > 0;
    }

    public void updateGroupName(Long groupId, String groupName) {
        String sql = "UPDATE files_group SET group_name = ?, update_date = sysdatetime() WHERE group_id = ?";
        sqlRepository.update(
            sql, 
            (ps, v) -> {
                ps.setString(1, groupName);
                ps.setLong(2, groupId);
            });
    }

    public void updateState(Long groupId, int state) {
        String sql = """
            UPDATE files_group
            SET state = ?
            WHERE group_id = ?
        """;

        sqlRepository.update(
            sql, 
            (ps, v) -> {
                ps.setInt(1, state);
                ps.setLong(2, groupId);
            });
    }
}
