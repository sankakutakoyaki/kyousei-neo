// package com.kyouseipro.neo.common.file.repository;

// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Repository;

// import com.kyouseipro.neo.common.file.entity.ConstructionFileGroupEntity;
// import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

// @Repository
// @RequiredArgsConstructor
// public class ConstructionFileGroupRepository {

//     private final SqlRepository sqlRepository;

//     public Long insert(ConstructionFileGroupEntity gruoup) {

//         String sql = """
//             INSERT INTO construction_file_group (
//                 construction_id,
//                 group_name,
//                 display_order
//             )
//             OUTPUT INSERTED.group_id
//             VALUES (?, ?, 0)
//         """;

//         return sqlRepository.insert(
//             sql,
//             (ps, g) -> {
//                 ps.setLong(1, g.getConstructionId());
//                 ps.setString(2, g.getGroupName());
//             },
//             rs -> rs.getLong(1),
//             gruoup
//         );
//     }

//     public boolean exists(Long groupId) {

//         String sql = """
//             SELECT COUNT(*)
//             FROM construction_file_group
//             WHERE group_id = ?
//         """;

//         Integer count = sqlRepository.queryOne(
//             sql,
//             (ps, id) -> ps.setLong(1, id),
//             rs -> rs.getInt(1),
//             groupId
//         );

//         return count != null && count > 0;
//     }

//     public void updateGroupName(Long groupId, String groupName) {
//         String sql = "UPDATE construction_file_group SET group_name = ?, update_date = sysdatetime() WHERE group_id = ?";
//         sqlRepository.update(
//             sql, 
//             (ps, v) -> {
//                 ps.setString(1, groupName);
//                 ps.setLong(2, groupId);
//             });
//     }
// }