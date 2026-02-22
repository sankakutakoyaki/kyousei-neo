// package com.kyouseipro.neo.common.file.repository;

// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.stereotype.Repository;

// import com.kyouseipro.neo.common.file.entity.FileEntity;
// import com.kyouseipro.neo.common.file.mappaer.FileEntityMapper;
// import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

// import lombok.RequiredArgsConstructor;

// @Repository
// @RequiredArgsConstructor
// public class EntityFileRepository {

//     private final SqlRepository sqlRepository;

//     public List<FileEntity> find(String entity, Long id) {

//         String sql = """
//             SELECT *
//             FROM entity_files
//             WHERE entity_name = ?
//             AND entity_id = ?
//             AND state = 1
//             ORDER BY file_group, entity_file_id
//         """;

//         return sqlRepository.queryList(
//             sql, 
//             (ps, v) -> {
//                 ps.setString(1, entity);
//                 ps.setLong(2, id);
//             },
//             FileEntityMapper::map
//         );
//     }

//     public Long insert(FileEntity f) {

//         String sql = """
//             INSERT INTO entity_files
//             (entity_name, entity_id, file_name, internal_name,
//              folder_name, file_size, content_type)
//             OUTPUT INSERTED.entity_file_id
//             VALUES (?, ?, ?, ?, ?, ?, ?)
//         """;

//         return sqlRepository.insert(
//             sql, 
//             (ps, v) -> {
//                 int i = 1;
//                 ps.setString(i++, f.getEntityName());
//                 ps.setLong(i++, f.getEntityId());
//                 ps.setString(i++, f.getFileName());
//                 ps.setString(i++, f.getInternalName());
//                 ps.setString(i++, f.getFolderName());
//                 ps.setLong(i++, f.getFileSize());
//                 ps.setString(i++, f.getContentType());
//             }, 
//             rs -> rs.getLong(1),
//             null
//         );
//     }

//     public void logicalDelete(Long fileId) {
//         String sql = """
//             UPDATE entity_files
//             SET state = 0,
//                 updated_at = SYSDATETIME()
//             WHERE entity_file_id = ?
//         """;

//         sqlRepository.queryOne(
//             sql, 
//             (ps, v) -> {
//                 ps.setLong(1, fileId);
//             },
//             null
//         );
//     }

//     public FileEntity findById(Long fileId) {

//         String sql = """
//             SELECT *
//             FROM entity_files
//             WHERE entity_file_id = ?
//             AND state = 1
//         """;

//         return sqlRepository.queryOne(
//             sql,
//             (ps, v) -> {
//                 ps.setLong(1, fileId);
//             },
//             FileEntityMapper::map
//         );
//     }
// }