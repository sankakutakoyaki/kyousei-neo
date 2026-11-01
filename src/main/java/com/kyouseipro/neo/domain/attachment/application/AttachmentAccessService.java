package com.kyouseipro.neo.domain.attachment.application;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.security.OrderScopeGuard;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentAccessService {
    private final OrderScopeGuard guard;
    private final SqlRepository sql;

    public void parent(Authentication auth, String type, long id, boolean write) {
        if ("ORDER".equalsIgnoreCase(type == null ? "" : type.trim()))
            guard.require(auth, id, write);
    }

    public void group(Authentication auth, long id, boolean write) {
        check(auth,
                sql.selectMap(
                        "SELECT parent_type,parent_id FROM attachment_groups WHERE attachment_group_id=? AND state=0",
                        List.of(id)),
                write);
    }

    public void file(Authentication auth, long id, boolean write) {
        check(auth, sql.selectMap(
                "SELECT g.parent_type,g.parent_id FROM attachments a JOIN attachment_groups g ON g.attachment_group_id=a.attachment_group_id AND g.state=0 WHERE a.attachment_id=? AND a.state=0",
                List.of(id)), write);
    }

    private void check(Authentication auth, List<Map<String, Object>> rows, boolean write) {
        if (rows.isEmpty())
            throw new AccessDeniedException("添付ファイルを操作できません。");
        var row = rows.get(0);
        parent(auth, row.get("parentType").toString(), ((Number) row.get("parentId")).longValue(), write);
    }
}
