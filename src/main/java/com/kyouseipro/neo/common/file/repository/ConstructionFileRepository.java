// package com.kyouseipro.neo.common.file.repository;

// import lombok.RequiredArgsConstructor;

// import java.util.List;

// import org.springframework.stereotype.Repository;

// import com.kyouseipro.neo.common.Enums;
// import com.kyouseipro.neo.common.file.entity.ConstructionFileDto;
// import com.kyouseipro.neo.common.file.entity.ConstructionFileEntity;
// import com.kyouseipro.neo.common.file.mappaer.ConstructionFileDtoMapper;
// import com.kyouseipro.neo.common.file.mappaer.ConstructionFileMapper;
// import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

// @Repository
// @RequiredArgsConstructor
// public class ConstructionFileRepository {


//     private final SqlRepository sqlRepository;

//     public Long insert(ConstructionFileEntity entity) {

//         String sql = """
//             INSERT INTO construction_file (
//                 group_id,
//                 construction_id,
//                 stored_name,
//                 original_name,
//                 display_name,
//                 file_type,
//                 mime_type,
//                 file_size,
//                 width,
//                 height,
//                 display_order
//             )
//             OUTPUT INSERTED.file_id
//             VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
//         """;

//         return sqlRepository.insert(
//             sql,
//             (ps, en) -> {
//                 ps.setLong(1, en.getGroupId());
//                 ps.setLong(2, en.getConstructionId());
//                 ps.setString(3, en.getStoredName());
//                 ps.setString(4, en.getOriginalName());
//                 ps.setString(5, en.getDisplayName());
//                 ps.setString(6, en.getFileType());
//                 ps.setString(7, en.getMimeType());
//                 ps.setLong(8, en.getFileSize());
//                 ps.setObject(9, en.getWidth());
//                 ps.setObject(10, en.getHeight());
//                 ps.setInt(11, en.getDisplayOrder());
//             },
//             rs -> rs.getLong(1),
//             entity
//         );
//     }

//     public int getNextDisplayOrder(Long groupId) {

//         String sql = """
//             SELECT ISNULL(MAX(display_order), 0)
//             FROM construction_file
//             WHERE group_id = ?
//         """;

//         Integer max = sqlRepository.queryOneOrNull(
//             sql,
//             (ps, id) -> ps.setLong(1, id),
//             rs -> rs.getInt(1),
//             groupId
//         );

//         return (max == null ? 0 : max) + 1;
//     }

//     public void decrementDisplayOrderAfter(
//             Long groupId,
//             int deletedOrder
//     ) {

//         String sql = """
//             UPDATE construction_file
//             SET display_order = display_order - 1
//             WHERE group_id = ?
//             AND display_order > ?
//             AND state = ?;
//         """;

//         sqlRepository.update(
//             sql,
//             (ps, p) -> {
//                 ps.setLong(1, groupId);
//                 ps.setInt(2, deletedOrder);
//                 ps.setInt(3, Enums.state.CREATE.getCode());
//             }
//         );
//     }

//     public List<ConstructionFileDto> findFiles(Long constructionId) {

//         String sql = """
//             SELECT
//                 f.file_id,
//                 f.original_name,
//                 f.display_name,
//                 f.group_id,
//                 g.group_name
//             FROM construction_file f
//             JOIN construction_file_group g
//             ON f.group_id = g.group_id
//             WHERE f.construction_id = ?
//             AND f.state <> ?
//             ORDER BY g.display_order, f.display_order
//         """;

//         return sqlRepository.queryList(
//             sql,
//             (ps, id) -> {
//                 ps.setLong(1, id);
//                 ps.setInt(2, Enums.state.DELETE.getCode());
//             },
//             ConstructionFileDtoMapper::map,
//             constructionId
//         );
//     }

//     public void delete(Long fileId) {

//         String sql = """
//             UPDATE construction_file
//             SET
//                 state = ?,
//                 update_date = sysdatetime()
//             WHERE
//                 file_id = ?
//                 AND state = ?;
//         """;

//         sqlRepository.update(
//             sql,
//             (ps, p) -> {
//                 ps.setLong(1, Enums.state.DELETE.getCode());
//                 ps.setLong(2, fileId);
//                 ps.setInt(3, Enums.state.CREATE.getCode());
//             },
//             null
//         );
//     }

//     // public ConstructionFileEntity findById(Long fileId) {

//     //     String sql = """
//     //         SELECT *
//     //         FROM construction_file
//     //         WHERE 
//     //             file_id = ? 
//     //             AND state = ?
//     //     """;

//     //     return sqlRepository.queryOne(
//     //         sql,
//     //         (ps, v) -> {
//     //             ps.setLong(1, fileId);
//     //             ps.setLong(2, Enums.state.CREATE.getCode());
//     //         },
//     //         ConstructionFileMapper::map
//     //     );
//     // }

//     /**
//      * 同名のdisplay_nameがないかチェックする
//      * @param groupId
//      * @param displayName
//      * @return
//      */
//     public boolean existsDisplayName(Long groupId, Long fileId, String displayName) {

//         String sql = """
//             SELECT 1
//             FROM construction_file
//             WHERE group_id = ?
//             AND display_name = ?
//             AND state <> ?
//         """;

//         if (fileId != null) {
//             sql += " AND file_id <> ?";
//         }

//         Integer count = sqlRepository.queryOneOrNull(
//             sql,
//             (ps, p) -> {
//                 int idx = 1;
//                 ps.setLong(idx++, groupId);
//                 ps.setString(idx++, displayName);
//                 ps.setInt(idx++, Enums.state.DELETE.getCode());
//                 if (fileId != null) {
//                     ps.setLong(idx++, fileId);
//                 }
                
//             },
//             rs -> rs.getInt(1)
//         );

//         return count != null && count > 0;
//     }

//     // public void updateDisplayName(Long fileId, String displayName) {

//     //     String sql = """
//     //         UPDATE construction_file
//     //         SET display_name = ?,
//     //             update_date = sysdatetime()
//     //         WHERE file_id = ?
//     //         AND state <> ?
//     //     """;

//     //     sqlRepository.update(
//     //         sql,
//     //         (ps, p) -> {
//     //             ps.setString(1, displayName);
//     //             ps.setLong(2, fileId);
//     //             ps.setInt(3, Enums.state.DELETE.getCode());
//     //         },
//     //         null
//     //     );
//     // }

//     public ConstructionFileEntity findById(Long fileId) {
//         String sql = "SELECT * FROM construction_file WHERE file_id = ?";
//         return sqlRepository.queryOne(
//             sql, 
//             (ps, v) -> ps.setLong(1, fileId),
//             rs -> {
//                 ConstructionFileEntity e = new ConstructionFileEntity();
//                 e.setFileId(rs.getLong("file_id"));
//                 e.setGroupId(rs.getLong("group_id"));
//                 e.setDisplayName(rs.getString("display_name"));
//                 return e;
//             });
//     }

//     public boolean existsDisplayName(Long groupId, String displayName) {
//         String sql = "SELECT COUNT(*) FROM construction_file WHERE group_id = ? AND display_name = ?";
//         Integer count = sqlRepository.queryOneOrNull(
//             sql, 
//             (ps, v) -> {
//                 ps.setLong(1, groupId);
//                 ps.setString(2, displayName);
//             }, 
//             rs -> rs.getInt(1));
//         return count != null && count > 0;
//     }

//     public void updateDisplayName(Long fileId, String displayName) {
//         String sql = "UPDATE construction_file SET display_name = ?, update_date = sysdatetime() WHERE file_id = ?";
//         sqlRepository.update(
//             sql, 
//             (ps, v) -> {
//                 ps.setString(1, displayName);
//                 ps.setLong(2, fileId);
//             });
//     }
// }