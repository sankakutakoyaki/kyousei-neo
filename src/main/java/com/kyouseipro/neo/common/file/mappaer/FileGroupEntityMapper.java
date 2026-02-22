package com.kyouseipro.neo.common.file.mappaer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.kyouseipro.neo.common.file.entity.FileGroupEntity;

public class FileGroupEntityMapper {
    public static FileGroupEntity map(ResultSet rs) throws SQLException {

        FileGroupEntity group = new FileGroupEntity();

        group.setGroupId(rs.getLong("group_id"));
        group.setParentType(rs.getString("parent_type"));
        group.setParentId(rs.getLong("parent_id"));
        group.setGroupName(rs.getString("group_name"));

        if (rs.getDate("work_date") != null) {
            group.setWorkDate(rs.getDate("work_date").toLocalDate());
        }

        group.setDisplayOrder(rs.getInt("display_order"));
        group.setNote(rs.getString("note"));

        Timestamp createTs = rs.getTimestamp("create_date");
        if (createTs != null) {
            group.setCreateDate(createTs.toLocalDateTime());
        }

        Timestamp updateTs = rs.getTimestamp("update_date");
        if (updateTs != null) {
            group.setUpdateDate(updateTs.toLocalDateTime());
        }

        group.setState(rs.getInt("state"));

        return group;
    }
}
