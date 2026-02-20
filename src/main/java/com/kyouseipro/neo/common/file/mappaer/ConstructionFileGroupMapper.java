package com.kyouseipro.neo.common.file.mappaer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.kyouseipro.neo.common.file.entity.ConstructionFileGroupEntity;

public class ConstructionFileGroupMapper {

    public static ConstructionFileGroupEntity map(ResultSet rs) throws SQLException {

        ConstructionFileGroupEntity group = new ConstructionFileGroupEntity();

        group.setGroupId(rs.getLong("group_id"));
        group.setConstructionId(rs.getLong("construction_id"));
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

        return group;
    }
}