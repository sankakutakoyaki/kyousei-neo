package com.kyouseipro.neo.common.file.mappaer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.kyouseipro.neo.common.file.entity.FileEntity;

public class FileEntityMapper {

    public static FileEntity map(ResultSet rs) throws SQLException {
        FileEntity file = new FileEntity();
        file.setFileId(rs.getLong("file_id"));
        file.setGroupId(rs.getLong("group_id"));
        file.setParentId(rs.getLong("parent_id"));
        file.setParentType(rs.getString("parent_type"));
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

        file.setState(rs.getInt("state"));

        return file;
    }
}
