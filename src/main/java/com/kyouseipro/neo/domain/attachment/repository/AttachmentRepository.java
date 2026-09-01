package com.kyouseipro.neo.domain.attachment.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.kyouseipro.neo.domain.attachment.model.AttachmentFile;
import com.kyouseipro.neo.domain.attachment.model.AttachmentGroup;
import com.kyouseipro.neo.domain.attachment.model.AttachmentItem;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AttachmentRepository {
    private final SqlRepository sqlRepository;

    public List<AttachmentGroup> findGroups(String parentType, long parentId) {
        String sql = """
            SELECT attachment_group_id, group_name FROM attachment_groups
            WHERE parent_type = ? AND parent_id = ? AND state = 0
            ORDER BY display_order, attachment_group_id
        """;
        return sqlRepository.queryList(sql, (ps, ignored) -> {
            ps.setString(1, parentType); ps.setLong(2, parentId);
        }, rs -> {
            long id = rs.getLong("attachment_group_id");
            return new AttachmentGroup(id, rs.getString("group_name"), findItems(id));
        }, null);
    }

    public List<AttachmentItem> findItems(long groupId) {
        String sql = """
            SELECT attachment_id, display_name, file_type, mime_type, file_size, width, height
            FROM attachments WHERE attachment_group_id = ? AND state = 0
            ORDER BY display_order, attachment_id
        """;
        return sqlRepository.queryList(sql, (ps, ignored) -> ps.setLong(1, groupId), rs ->
            new AttachmentItem(rs.getLong("attachment_id"), rs.getString("display_name"),
                rs.getString("file_type"), rs.getString("mime_type"), rs.getLong("file_size"),
                (Integer) rs.getObject("width"), (Integer) rs.getObject("height")), null);
    }

    public long insertGroup(String parentType, long parentId, String name) {
        String sql = """
            INSERT INTO attachment_groups(parent_type,parent_id,group_name,display_order)
            OUTPUT INSERTED.attachment_group_id
            VALUES(?,?,?,(SELECT COALESCE(MAX(display_order),-1)+1 FROM attachment_groups WHERE parent_type=? AND parent_id=? AND state=0))
        """;
        return sqlRepository.insert(sql, (ps, ignored) -> {
            ps.setString(1,parentType); ps.setLong(2,parentId); ps.setString(3,name);
            ps.setString(4,parentType); ps.setLong(5,parentId);
        }, rs -> rs.getLong(1), null);
    }

    public boolean groupBelongsTo(long groupId, String parentType, long parentId) {
        return sqlRepository.queryOneOrNull(
            "SELECT attachment_group_id FROM attachment_groups WHERE attachment_group_id=? AND parent_type=? AND parent_id=? AND state=0",
            (ps, ignored) -> { ps.setLong(1,groupId); ps.setString(2,parentType); ps.setLong(3,parentId); },
            rs -> rs.getLong(1), null) != null;
    }

    public long insertFile(long groupId, String stored, String original, String type, String mime,
                           long size, Integer width, Integer height) {
        String sql = """
            INSERT INTO attachments(attachment_group_id,stored_name,original_name,display_name,file_type,mime_type,file_size,width,height,display_order)
            OUTPUT INSERTED.attachment_id
            VALUES(?,?,?,?,?,?,?,?,?,(SELECT COALESCE(MAX(display_order),-1)+1 FROM attachments WHERE attachment_group_id=? AND state=0))
        """;
        return sqlRepository.insert(sql, (ps, ignored) -> {
            ps.setLong(1,groupId); ps.setString(2,stored); ps.setString(3,original); ps.setString(4,original);
            ps.setString(5,type); ps.setString(6,mime); ps.setLong(7,size);
            if(width == null) ps.setNull(8, java.sql.Types.INTEGER); else ps.setInt(8,width);
            if(height == null) ps.setNull(9, java.sql.Types.INTEGER); else ps.setInt(9,height);
            ps.setLong(10,groupId);
        }, rs -> rs.getLong(1), null);
    }

    public AttachmentFile findFile(long id) {
        return sqlRepository.queryOneOrNull("""
            SELECT a.display_name,a.mime_type,a.stored_name,a.attachment_group_id
            FROM attachments a JOIN attachment_groups g ON g.attachment_group_id=a.attachment_group_id
            WHERE a.attachment_id=? AND a.state=0 AND g.state=0
        """, (ps, ignored) -> ps.setLong(1,id), rs -> new AttachmentFile(rs.getString(1), rs.getString(2),
            java.nio.file.Path.of(Long.toString(rs.getLong(4)), rs.getString(3))), null);
    }

    public List<AttachmentFile> findFilesInGroup(long groupId) {
        return sqlRepository.queryList("SELECT display_name,mime_type,stored_name FROM attachments WHERE attachment_group_id=? AND state=0",
            (ps, ignored) -> ps.setLong(1,groupId), rs -> new AttachmentFile(rs.getString(1),rs.getString(2),
                java.nio.file.Path.of(Long.toString(groupId),rs.getString(3))), null);
    }

    public void renameGroup(long id, String name) { sqlRepository.updateRequired(
        "UPDATE attachment_groups SET group_name=?,update_date=SYSDATETIME(),version=version+1 WHERE attachment_group_id=? AND state=0",
        List.of(name,id), "フォルダが見つかりません。"); }
    public void renameFile(long id, String name) { sqlRepository.updateRequired(
        "UPDATE attachments SET display_name=?,update_date=SYSDATETIME(),version=version+1 WHERE attachment_id=? AND state=0",
        List.of(name,id), "ファイルが見つかりません。"); }
    public void deleteFile(long id) { sqlRepository.updateRequired(
        "UPDATE attachments SET state=1,update_date=SYSDATETIME(),version=version+1 WHERE attachment_id=? AND state=0",
        List.of(id), "ファイルが見つかりません。"); }
    public void deleteGroup(long id) {
        sqlRepository.updateRequired("UPDATE attachment_groups SET state=1,update_date=SYSDATETIME(),version=version+1 WHERE attachment_group_id=? AND state=0",
            List.of(id), "フォルダが見つかりません。");
        sqlRepository.update("UPDATE attachments SET state=1,update_date=SYSDATETIME(),version=version+1 WHERE attachment_group_id=? AND state=0", List.of(id));
    }
}
