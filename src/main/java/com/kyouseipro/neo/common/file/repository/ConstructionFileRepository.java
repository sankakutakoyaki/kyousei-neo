package com.kyouseipro.neo.common.file.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.file.entity.ConstructionFileEntity;
import com.kyouseipro.neo.common.file.mappaer.ConstructionFileMapper;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConstructionFileRepository {

    private final SqlRepository sqlRepository;

    /**
     * グループ単位で取得
     */
    public List<ConstructionFileEntity> findByGroupId(Long groupId) {

        return sqlRepository.queryList(
            """
            SELECT *
            FROM construction_file
            WHERE group_id = ?
            ORDER BY display_order, file_id
            """,
            (ps, v) -> ps.setLong(1, groupId),
            ConstructionFileMapper::map
        );
    }

    /**
     * ファイル登録
     */
    public Long insert(ConstructionFileEntity file) {

        return sqlRepository.queryOne(
            """
            INSERT INTO construction_file
            (group_id, construction_id,
             stored_name, original_name, display_name,
             file_type, mime_type, file_size,
             width, height, display_order,
             create_date, update_date)
            OUTPUT INSERTED.file_id
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME(), SYSDATETIME())
            """,
            (ps, v) -> {
                int i = 1;
                ps.setLong(i++, file.getGroupId());
                ps.setLong(i++, file.getConstructionId());
                ps.setString(i++, file.getStoredName());
                ps.setString(i++, file.getOriginalName());
                ps.setString(i++, file.getDisplayName());
                ps.setString(i++, file.getFileType());
                ps.setString(i++, file.getMimeType());
                ps.setLong(i++, file.getFileSize());

                if (file.getWidth() != null) {
                    ps.setInt(i++, file.getWidth());
                } else {
                    ps.setNull(i++, java.sql.Types.INTEGER);
                }

                if (file.getHeight() != null) {
                    ps.setInt(i++, file.getHeight());
                } else {
                    ps.setNull(i++, java.sql.Types.INTEGER);
                }

                ps.setInt(i++, file.getDisplayOrder());
            },
            rs -> rs.getLong(1)
        );
    }

    /**
     * ファイル削除
     */
    public void delete(Long fileId) {

        sqlRepository.update(
            "DELETE FROM construction_file WHERE file_id = ?",
            (ps, v) -> ps.setLong(1, fileId)
        );
    }

    /**
     * 並び順更新
     */
    public void updateDisplayOrder(Long fileId, Integer order) {

        sqlRepository.update(
            """
            UPDATE construction_file
            SET display_order = ?, update_date = SYSDATETIME()
            WHERE file_id = ?
            """,
            (ps, v) -> {
                ps.setInt(1, order);
                ps.setLong(2, fileId);
            }
        );
    }

    /**
     * 
     */
    public int save(ConstructionFileEntity entity) {
        String sql = """
            INSERT INTO construction_file
            (group_id, original_name, stored_name, file_type, file_size, created_at)
            VALUES (?, ?, ?, ?, ?, ?);
        """;

        return sqlRepository.insert(
            sql,
            (ps, e) -> {
                int i = 1;
                ps.setLong(i++, e.getGroupId());
                ps.setString(i++, e.getOriginalName());
                ps.setString(i++, e.getStoredName());
                ps.setString(i++, e.getFileType());
                ps.setLong(i++, e.getFileSize());
                ps.setTimestamp(i++, Timestamp.valueOf(e.getCreateDate()));
            },
            rs -> rs.getInt("file_id"), // OUTPUTで取得する場合
            entity
        );
    }
}