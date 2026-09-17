package com.kyouseipro.neo.domain.operations;

import java.util.*;
import org.springframework.stereotype.Repository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@Repository @RequiredArgsConstructor
public class OperationRepository {
    private final SqlRepository sql;
    public Map<String,Object> get(OperationSpec spec,long id,boolean lock) {
        var rows=sql.selectMap("SELECT * FROM " + spec.table() + (lock ? " WITH (UPDLOCK,HOLDLOCK)" : "") + " WHERE id=? AND state=0",List.of(id));
        if(rows.isEmpty()) throw new BusinessException("データが削除・変更されています。開き直してください。");
        return rows.get(0);
    }
    public long save(OperationSpec spec,long id,Map<String,Object> values,String editor) {
        // specで定義した列だけを扱う。テーブル名や列名は要求から受け取らない。
        var allowed=new HashSet<>(spec.columns());
        allowed.removeAll(List.of("id","version","regist_date","update_date"));
        if(!allowed.containsAll(values.keySet())) throw new IllegalArgumentException("Unknown operation column");
        var params=new ArrayList<Object>(values.values());
        if(id==0) {
            String cols=String.join(",",values.keySet());
            String markers=String.join(",",Collections.nCopies(values.size(),"?"));
            id=sql.insert("INSERT " + spec.table()+" ("+cols+") OUTPUT INSERTED.id VALUES ("+markers+")",params,rs->rs.getLong(1));
            log(spec,id,"INSERT",editor);
        } else {
            params.add(id);
            String sets=String.join(",",values.keySet().stream().map(k->k+"=?").toList());
            sql.update("UPDATE "+spec.table()+" SET "+sets+",version=version+1,update_date=SYSDATETIME() WHERE id=? AND state=0",params);
            log(spec,id,Objects.equals(values.get("state"),9)?"DELETE":"UPDATE",editor);
        }
        return id;
    }
    public void log(OperationSpec spec,long id,String process,String editor) {
        String cols=String.join(",",spec.columns());
        sql.update("INSERT "+spec.table()+"_log ("+cols+",editor,process,log_date) SELECT "+cols+",?,?,SYSDATETIME() FROM "+spec.table()+" WHERE id=?",List.of(editor,process,id));
    }
    public static void version(Map<String,Object> row,Object expected) {
        if(expected==null || !String.valueOf(row.get("version")).equals(String.valueOf(expected)))
            throw new BusinessException("他の画面で更新されています。開き直して確認してください。");
    }
    // saveの配車ロック内で採番する。無効化した車両も含め、番号を再利用しない。
    public String nextVehicleCode() {
        var rows=sql.selectMap("SELECT COALESCE(MAX(TRY_CONVERT(BIGINT,code)),999)+1 AS next_code FROM operation_vehicle WHERE TRY_CONVERT(BIGINT,code)>=1000",List.of());
        return rows.get(0).get("nextCode").toString();
    }
    public void schedulingLock() {
        sql.update("""
            DECLARE @result INT;
            EXEC @result=sys.sp_getapplock @Resource=N'kyousei-operation-scheduling', @LockMode='Exclusive', @LockOwner='Transaction', @LockTimeout=10000;
            IF @result < 0 THROW 51000, N'他の配車を更新中です。再度お試しください。', 1;
            """,List.of());
    }
}
