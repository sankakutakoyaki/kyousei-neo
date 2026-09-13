package com.kyouseipro.neo.domain.operations;

import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;

@Component @RequiredArgsConstructor
public class OperationAttachmentAccess {
    private final SqlRepository sql;
    private final OperationAccess access;
    private final OperationRepository repo;
    public boolean parent(String type,long id,boolean write) {
        type=Objects.toString(type,"").trim().toUpperCase(Locale.ROOT);
        if(!type.startsWith("OP_")) return false;
        OperationSpec spec=switch(type) { case "OP_QUALIFICATION"->OperationSpec.QUALIFICATION; case "OP_INSPECTION"->OperationSpec.INSPECTION; default->throw new AccessDeniedException("添付先が不正です。"); };
        access.read(spec);
        if(spec==OperationSpec.QUALIFICATION && !access.privateAccess() || write && !access.canWrite(spec)) throw new AccessDeniedException("添付書類を扱う権限がありません。");
        repo.get(spec,id,false); return true;
    }
    public boolean group(long id,boolean write) {
        var rows=sql.selectMap("SELECT parent_type,parent_id FROM attachment_groups WHERE attachment_group_id=?",List.of(id));
        return !rows.isEmpty() && parent(rows.get(0).get("parentType").toString(),((Number)rows.get(0).get("parentId")).longValue(),write);
    }
    public boolean file(long id,boolean write) {
        var rows=sql.selectMap("SELECT g.parent_type,g.parent_id FROM attachments a JOIN attachment_groups g ON g.attachment_group_id=a.attachment_group_id WHERE a.attachment_id=?",List.of(id));
        return !rows.isEmpty() && parent(rows.get(0).get("parentType").toString(),((Number)rows.get(0).get("parentId")).longValue(),write);
    }
}
