package com.kyouseipro.neo.domain.operations;

import java.util.*;
import java.sql.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import static com.kyouseipro.neo.domain.operations.OperationService.*;

@Service @RequiredArgsConstructor
public class OperationScheduleService {
    private final SqlRepository sql;
    private final OperationRepository repo;
    private final OperationAccess access;
    public List<Map<String,Object>> members(long crewId) {
        return sql.selectMap("SELECT employee_id,employee_code,employee_name,is_driver FROM operation_crew_members WHERE crew_id=? ORDER BY employee_id",List.of(crewId));
    }
    public List<Map<String,Object>> impacts(long crewId) {
        return sql.selectMap("""
            SELECT o.order_id,o.request_number,o.title,o.version AS order_version,p.version,p.leader_id
            FROM operation_order_vehicles v JOIN orders o ON o.order_id=v.order_id
            JOIN operation_order_plans p ON p.order_id=o.order_id
            WHERE v.crew_id=? AND o.state=0 ORDER BY o.order_id
            """,List.of(crewId));
    }
    public void logMembers(long crewId,int version,String process) {
        sql.update("""
            INSERT operation_crew_members_log(crew_id,employee_id,employee_code,employee_name,is_driver,version,editor,process,log_date)
            SELECT crew_id,employee_id,employee_code,employee_name,is_driver,?,?,?,SYSDATETIME() FROM operation_crew_members WHERE crew_id=?
            """,List.of(version,access.editor(),process,crewId));
    }
    public long saveCrew(long key,Map<String,Object> old,Map<String,Object> values,Map<String,Object> p,Map<String,Object> vehicle) {
        if(key!=0) { immutable(old.get("workDate"),values.get("work_date"),"登録済み編成の運行日"); immutable(old.get("vehicleCode"),values.get("vehicle_code"),"登録済み編成の車両"); }
        if(!(p.get("members") instanceof List<?> input) || input.isEmpty()) throw new BusinessException("乗車メンバーを登録してください。");
        int capacity=positiveInteger(vehicle.get("capacity"),"乗車定員");
        if(input.size()>capacity) throw new BusinessException("乗車定員を超えています。");
        var selected=new LinkedHashMap<Long,Map<String,Object>>();
        int drivers=0;
        for(Object item:input) {
            if(!(item instanceof Map<?,?> m)) throw new BusinessException("メンバーの指定が不正です。");
            String code=required(m.get("employeeCode"),"担当者コード",120);
            var found=sql.selectMap("""
                SELECT e.employee_id,e.code AS employee_code,e.full_name AS employee_name FROM employees e
                LEFT JOIN companies c ON c.company_id=e.company_id
                WHERE e.code=? AND e.state=0 AND e.category IN (1,2,3) AND (ISNULL(e.company_id,0)=0 OR c.state=0)
                """,List.of(code));
            if(found.size()!=1) throw new BusinessException("担当者コード「"+code+"」が未登録・無効または重複しています。");
            var row=new LinkedHashMap<>(found.get(0)); long employeeId=((Number)row.get("employeeId")).longValue();
            boolean driver=Boolean.TRUE.equals(m.get("isDriver")); row.put("isDriver",driver); if(driver) drivers++;
            if(selected.put(employeeId,row)!=null) throw new BusinessException("担当者が重複しています。");
            if(!sql.selectMap("SELECT c.id FROM operation_crew c JOIN operation_crew_members m ON m.crew_id=c.id WHERE c.work_date=? AND c.state=0 AND c.id<>? AND m.employee_id=?",List.of(values.get("work_date"),key,employeeId)).isEmpty()) throw new BusinessException("「"+row.get("employeeName")+"」は同日の別車両に登録されています。");
        }
        if(drivers!=1) throw new BusinessException("ドライバーを1名指定してください。");
        if(!sql.selectMap("SELECT id FROM operation_crew WHERE work_date=? AND vehicle_id=? AND state=0 AND id<>?",List.of(values.get("work_date"),vehicle.get("id"),key)).isEmpty()) throw new BusinessException("その日の車両編成は登録済みです。");
        available(vehicle.get("id"),(Date)values.get("work_date"));
        var impacts=key==0?List.<Map<String,Object>>of():impacts(key);
        if(!impacts.isEmpty()) {
            if(!impactTokens(impacts).equals(impactTokens(p.get("impacts")))) throw new BusinessException("影響する伝票が変更されました。開き直して確認してください。");
            for(var order:impacts) {
                Object leader=order.get("leaderId");
                if(leader!=null && !selected.containsKey(((Number)leader).longValue())) {
                    var others=sql.selectMap("SELECT employee_id FROM operation_order_members WHERE order_id=? AND crew_id<>? AND employee_id=?",List.of(order.get("orderId"),key,leader));
                    if(others.isEmpty()) throw new BusinessException("現場責任者が乗車メンバーから外れます。先に伝票「"+order.get("requestNumber")+"」の責任者を変更してください。");
                }
            }
        }
        long crew=repo.save(OperationSpec.CREW,key,values,access.editor());
        int revision=key==0?0:((Number)old.get("version")).intValue()+1;
        if(key!=0) logMembers(crew,revision,"BEFORE");
        sql.update("DELETE FROM operation_crew_members WHERE crew_id=?",List.of(crew));
        for(var m:selected.values()) sql.update("INSERT operation_crew_members(crew_id,employee_id,employee_code,employee_name,is_driver) VALUES(?,?,?,?,?)",List.of(crew,m.get("employeeId"),m.get("employeeCode"),m.get("employeeName"),m.get("isDriver")));
        logMembers(crew,revision,key==0?"INSERT":"UPDATE");
        for(var order:impacts) {
            Object orderId=order.get("orderId");
            // 完了への変更と競合しないよう、状態をロックして再確認する。
            var locked=sql.selectMap("SELECT state,version FROM orders WITH(UPDLOCK,HOLDLOCK) WHERE order_id=?",List.of(orderId));
            if(locked.isEmpty() || !Objects.equals(locked.get(0).get("version"),order.get("orderVersion")) || ((Number)locked.get(0).get("state")).intValue()!=0) throw new BusinessException("伝票の状態が変更されました。開き直してください。");
            sql.update("DELETE FROM operation_order_members WHERE order_id=? AND crew_id=?",List.of(orderId,crew));
            copyMembers(orderId,crew);
            sql.update("UPDATE operation_order_plans SET version=version+1 WHERE order_id=?",List.of(orderId));
            logPlan(orderId,"CREW_UPDATE");
        }
        return crew;
    }
    public static Set<String> impactTokens(Object value) {
        var result=new TreeSet<String>();
        if(value instanceof List<?> list) for(Object item:list) if(item instanceof Map<?,?> p)
            result.add(p.get("orderId")+":"+p.get("version")+":"+p.get("orderVersion"));
        return result;
    }
    public void checkMaintenance(Map<String,Object> values) {
        if(values.get("unavailable_from")==null) return;
        var crews=sql.selectMap("SELECT id FROM operation_crew WHERE vehicle_id=? AND work_date>=? AND work_date<=? AND state=0",List.of(values.get("vehicle_id"),values.get("unavailable_from"),values.get("unavailable_to")));
        if(!crews.isEmpty()) throw new BusinessException("使用不可期間に登録済み編成があります。先に編成・配車を変更してください。");
    }
    public void available(Object vehicleId,Date day) {
        if(!sql.selectMap("SELECT id FROM operation_inspection WHERE vehicle_id=? AND state=0 AND unavailable_from<=? AND unavailable_to>=?",List.of(vehicleId,day,day)).isEmpty()) throw new BusinessException("点検・整備により使用不可の車両です。");
    }
    public List<Map<String,Object>> dispatchList(Map<String,Object> p) {
        access.read(OperationSpec.CREW);
        Date from=date(p.get("dateFrom"),true),to=date(p.get("dateTo"),true);
        if(to.before(from) || to.toLocalDate().isAfter(from.toLocalDate().plusDays(92))) throw new BusinessException("検索期間は93日以内にしてください。");
        return sql.selectMap("""
            SELECT o.order_id,o.request_number,o.title,o.visit_date,o.visit_time,o.full_address,o.version,o.state,
            p.version AS plan_version,p.leader_name,
            CAST(CASE WHEN EXISTS(SELECT 1 FROM operation_order_vehicles v WHERE v.order_id=o.order_id AND (v.work_date<>o.visit_date OR o.visit_date IS NULL)) THEN 1 ELSE 0 END AS BIT) AS schedule_mismatch,
            (SELECT STRING_AGG(CAST(v.vehicle_code+N' '+v.vehicle_name AS NVARCHAR(MAX)),N'、') FROM operation_order_vehicles v WHERE v.order_id=o.order_id) AS vehicle_names,
            (SELECT COUNT(*) FROM order_dispatch_assignments a WHERE a.order_id=o.order_id AND a.cancelled_at IS NULL) AS legacy_count
            FROM orders o LEFT JOIN operation_order_plans p ON p.order_id=o.order_id
            WHERE o.state IN(0,2) AND ((o.visit_date>=? AND o.visit_date<=?)
            """+(Boolean.TRUE.equals(p.get("includeUndated"))?" OR o.visit_date IS NULL":"")+") ORDER BY o.visit_date,o.visit_time,o.order_id",List.of(from,to));
    }
    @Transactional
    public Map<String,Object> dispatchDetail(Map<String,Object> p) {
        access.read(OperationSpec.CREW); repo.schedulingLock();
        long order=id(p.get("orderId"));
        var rows=sql.selectMap("""
            SELECT o.order_id,o.request_number,o.title,o.visit_date,o.visit_time,o.version AS order_version,o.state,
                COALESCE(p.version,0) AS version,p.leader_id,p.leader_name
            FROM orders o WITH(UPDLOCK,HOLDLOCK) LEFT JOIN operation_order_plans p ON p.order_id=o.order_id
            WHERE o.order_id=? AND o.state IN(0,2)
            """,List.of(order));
        if(rows.isEmpty()) throw new BusinessException("受注が存在しないか削除されています。");
        var result=new LinkedHashMap<>(rows.get(0)); result.put("id",order);
        result.put("vehicles",sql.selectMap("SELECT v.*,c.version FROM operation_order_vehicles v JOIN operation_crew c ON c.id=v.crew_id WHERE v.order_id=? ORDER BY v.crew_id",List.of(order)));
        result.put("members",sql.selectMap("SELECT * FROM operation_order_members WHERE order_id=? ORDER BY crew_id,employee_id",List.of(order)));
        result.put("history",sql.selectMap("SELECT * FROM operation_order_plans_log WHERE order_id=? ORDER BY log_id DESC",List.of(order)));
        result.put("legacyAssignments",sql.selectMap("SELECT employee_name,role,assigned_at,cancelled_at FROM order_dispatch_assignments WHERE order_id=? ORDER BY assignment_id",List.of(order)));
        return result;
    }
    @Transactional
    public Map<String,Object> crewLookup(Map<String,Object> p) {
        access.read(OperationSpec.CREW);
        var rows=sql.selectMap("SELECT c.*,v.name AS vehicle_name FROM operation_crew c JOIN operation_vehicle v ON v.id=c.vehicle_id WHERE c.state=0 AND v.state=0 AND c.work_date=? AND v.code=?",List.of(date(p.get("workDate"),true),required(p.get("vehicleCode"),"車両コード",120)));
        if(rows.size()!=1) throw new BusinessException("その日の日別編成がありません。先に乗車メンバーを登録してください。");
        var result=new LinkedHashMap<>(rows.get(0));
        var people=members(((Number)result.get("id")).longValue());
        for(var member:people) member.put("qualifications",sql.selectMap("SELECT qualification_name,acquired_date,expiry_date FROM operation_qualification WHERE employee_id=? AND state=0 ORDER BY acquired_date DESC",List.of(member.get("employeeId"))));
        result.put("members",people);
        result.put("warnings",warnings(((Number)result.get("id")).longValue(),(Date)date(p.get("workDate"),true)));
        return result;
    }
    private List<String> warnings(long crew,Date day) {
        var warnings=new ArrayList<String>();
        for(var m:members(crew)) {
            var licenses=sql.selectMap("""
                SELECT q.expiry_date FROM operation_qualification q JOIN operation_qualification_type t ON t.id=q.qualification_type_id
                WHERE q.employee_id=? AND q.state=0 AND t.state=0 AND t.driver_license=1 AND q.acquired_date<=?
                  AND (q.expiry_date>=? OR (q.expiry_date IS NULL AND t.expiry_required=0))
                """,List.of(m.get("employeeId"),day,day));
            if(Boolean.TRUE.equals(m.get("isDriver")) && licenses.isEmpty()) warnings.add(m.get("employeeName")+"：運転免許の有効な登録を確認できません。");
        }
        var due=sql.selectMap("SELECT i.kind,i.due_date FROM operation_inspection i JOIN operation_crew c ON c.vehicle_id=i.vehicle_id WHERE c.id=? AND i.state=0 AND i.performed_date IS NULL AND i.due_date<=DATEADD(day,30,?)",List.of(crew,day));
        for(var i:due) warnings.add(i.get("kind")+"の期限："+i.get("dueDate"));
        return warnings;
    }
    @Transactional
    public long dispatchSave(Map<String,Object> p) {
        access.write(OperationSpec.CREW,p); repo.schedulingLock();
        var current=dispatchDetail(Map.of("orderId",p.get("orderId")));
        if(((Number)current.get("state")).intValue()!=0) throw new BusinessException("完了済みの受注は変更できません。");
        OperationRepository.version(current,p.get("version"));
        immutable(current.get("orderVersion"),p.get("orderVersion"),"受注の更新番号（画面を開き直してください）");
        if(!(p.get("vehicles") instanceof List<?> input)) throw new BusinessException("車両の指定が不正です。");
        long order=id(p.get("orderId"));
        Date day=input.isEmpty()?null:date(current.get("visitDate"),true);
        var crews=new LinkedHashMap<Long,Map<String,Object>>(); var people=new LinkedHashMap<Long,String>();
        for(Object item:input) {
            if(!(item instanceof Map<?,?> v)) throw new BusinessException("車両の指定が不正です。");
            long crew=id(v.get("crewId")); var c=repo.get(OperationSpec.CREW,crew,true);
            OperationRepository.version(c,v.get("version"));
            if(!date(c.get("workDate"),true).equals(day)) throw new BusinessException("訪問日と編成の運行日が一致しません。");
            var vehicle=repo.get(OperationSpec.VEHICLE,((Number)c.get("vehicleId")).longValue(),true);
            available(vehicle.get("id"),day); c=new LinkedHashMap<>(c); c.put("vehicleName",vehicle.get("name"));
            if(crews.put(crew,c)!=null) throw new BusinessException("車両が重複しています。");
            var crewMembers=members(crew);
            if(crewMembers.isEmpty() || crewMembers.size()>positiveInteger(vehicle.get("capacity"),"乗車定員")) throw new BusinessException("乗車編成・定員を確認してください。");
            for(var m:crewMembers) {
                if(sql.selectMap("SELECT e.employee_id FROM employees e LEFT JOIN companies c ON c.company_id=e.company_id WHERE e.employee_id=? AND e.state=0 AND e.category IN(1,2,3) AND (ISNULL(e.company_id,0)=0 OR c.state=0)",List.of(m.get("employeeId"))).isEmpty())
                    throw new BusinessException("無効な担当者が含まれています。先に編成を更新してください。");
                people.put(((Number)m.get("employeeId")).longValue(),m.get("employeeName").toString());
            }
        }
        Long leader=p.get("leaderId")==null || p.get("leaderId").toString().isBlank()?null:id(p.get("leaderId"));
        if(!crews.isEmpty() && (leader==null || !people.containsKey(leader))) throw new BusinessException("乗車メンバーから現場責任者を指定してください。");
        if(crews.isEmpty()) leader=null;
        boolean exists=!sql.selectMap("SELECT order_id FROM operation_order_plans WHERE order_id=?",List.of(order)).isEmpty();
        if(!exists) sql.update("INSERT operation_order_plans(order_id,leader_id,leader_name,version) VALUES(?,?,?,1)",Arrays.asList(order,leader,leader==null?null:people.get(leader)));
        else sql.update("UPDATE operation_order_plans SET leader_id=?,leader_name=?,version=version+1 WHERE order_id=?",Arrays.asList(leader,leader==null?null:people.get(leader),order));
        sql.update("DELETE FROM operation_order_members WHERE order_id=?",List.of(order));
        sql.update("DELETE FROM operation_order_vehicles WHERE order_id=?",List.of(order));
        for(var entry:crews.entrySet()) {
            var c=entry.getValue();
            sql.update("INSERT operation_order_vehicles(order_id,crew_id,vehicle_code,vehicle_name,work_date) VALUES(?,?,?,?,?)",List.of(order,entry.getKey(),c.get("vehicleCode"),c.get("vehicleName"),day));
            copyMembers(order,entry.getKey());
        }
        logPlan(order,"UPDATE"); return order;
    }
    private void copyMembers(Object order,Object crew) {
        sql.update("INSERT operation_order_members(order_id,crew_id,employee_id,employee_code,employee_name,is_driver) SELECT ?,crew_id,employee_id,employee_code,employee_name,is_driver FROM operation_crew_members WHERE crew_id=?",List.of(order,crew));
    }
    private void logPlan(Object order,String process) {
        sql.update("""
            INSERT operation_order_plans_log(order_id,version,leader_id,leader_name,vehicles_json,members_json,editor,process,log_date)
            SELECT p.order_id,p.version,p.leader_id,p.leader_name,
              (SELECT v.* FROM operation_order_vehicles v WHERE v.order_id=p.order_id FOR JSON PATH),
              (SELECT m.* FROM operation_order_members m WHERE m.order_id=p.order_id FOR JSON PATH),?,?,SYSDATETIME()
            FROM operation_order_plans p WHERE p.order_id=?
            """,List.of(access.editor(),process,order));
    }
}
