package com.kyouseipro.neo.common.file.mappaer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.kyouseipro.neo.common.file.entity.ConstructionFileEntity;

public class ConstructionFileMapper {

    public static ConstructionFileEntity map(ResultSet rs) throws SQLException {

        ConstructionFileEntity file = new ConstructionFileEntity();

        file.setFileId(rs.getLong("file_id"));
        file.setGroupId(rs.getLong("group_id"));
        file.setConstructionId(rs.getLong("construction_id"));

        file.setStoredName(rs.getString("stored_name"));
        file.setOriginalName(rs.getString("original_name"));
        file.setDisplayName(rs.getString("display_name"));

        file.setFileType(rs.getString("file_type"));
        file.setMimeType(rs.getString("mime_type"));

        file.setFileSize(rs.getLong("file_size"));

        int width = rs.getInt("width");
        file.setWidth(rs.wasNull() ? null : width);

        int height = rs.getInt("height");
        file.setHeight(rs.wasNull() ? null : height);

        file.setDisplayOrder(rs.getInt("display_order"));

        Timestamp createTs = rs.getTimestamp("create_date");
        if (createTs != null) {
            file.setCreateDate(createTs.toLocalDateTime());
        }

        Timestamp updateTs = rs.getTimestamp("update_date");
        if (updateTs != null) {
            file.setUpdateDate(updateTs.toLocalDateTime());
        }

        return file;
    }
}