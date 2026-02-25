package com.kyouseipro.neo.common.file.mappaer;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.file.entity.FileDto;

public class FileDtoMapper {
    public static FileDto map(ResultSet rs) throws SQLException {
        FileDto file = new FileDto();

        file.setFileId(rs.getLong("file_id"));
        file.setParentId(rs.getLong("parent_id"));
        file.setParentType(rs.getString("parent_type"));
        file.setMimeType(rs.getString("mime_type"));
        file.setGroupId(rs.getLong("group_id"));

        file.setDisplayName(rs.getString("display_name"));
        file.setGroupName(rs.getString("group_name"));

        return file;
    }
}
