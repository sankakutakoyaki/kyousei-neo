package com.kyouseipro.neo.domain.operations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Date;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class OperationService {
    private final SqlRepository sql;
    private final OperationRepository repo;
    private final OperationAccess access;
    private final OperationScheduleService schedules;

    public List<Map<String,Object>> employeeQualifications(Map<String,Object> p) {
        access.read(OperationSpec.QUALIFICATION);
        return sql.selectMap("SELECT qualification_name,acquired_date,expiry_date,renewal_date FROM operation_qualification WHERE employee_id=? AND state=0 ORDER BY acquired_date DESC",List.of(id(p.get("employeeId"))));
    }
    public List<Map<String,Object>> list(Map<String,Object> p) {
        var spec=OperationSpec.from(p.get("entity")); access.read(spec);
        String select="*";
        if(spec==OperationSpec.QUALIFICATION && !access.privateAccess())
            select="id,version,state,employee_code,employee_name,qualification_name,acquired_date,expiry_date,renewal_date";
        if(spec==OperationSpec.CREW) {
            return sql.selectMap("""
                SELECT c.*,
                (SELECT STRING_AGG(CAST(m.employee_name AS NVARCHAR(MAX)),N'、') FROM operation_crew_members m WHERE m.crew_id=c.id) AS member_names,
                (SELECT m.employee_name FROM operation_crew_members m WHERE m.crew_id=c.id AND m.is_driver=1) AS driver_name
                FROM operation_crew c WHERE c.state=0 AND c.work_date=? ORDER BY c.vehicle_code
                """,List.of(date(p.get("workDate"),true)));
        }
        String where=" WHERE state=0";
        var args=new ArrayList<Object>();
        if(spec==OperationSpec.CREW || spec==OperationSpec.SCORE) {
            where+=" AND work_date=?"; args.add(date(p.get("workDate"),true));
        }
        return sql.selectMap("SELECT "+select+" FROM "+spec.table()+where+" ORDER BY id DESC",args);
    }
    @Transactional
    public Map<String,Object> detail(Map<String,Object> p) {
        var spec=OperationSpec.from(p.get("entity")); access.read(spec);
        if(spec==OperationSpec.QUALIFICATION && !access.privateAccess())
            throw new org.springframework.security.access.AccessDeniedException("資格の詳細は管理担当者のみ参照できます。");
        long id=id(p.get("id"));
        if(spec==OperationSpec.CREW) repo.schedulingLock();
        var data=new LinkedHashMap<>(repo.get(spec,id,true));
        data.put("history",sql.selectMap("SELECT * FROM "+spec.table()+"_log WHERE id=? ORDER BY log_id DESC",List.of(id)));
        if(spec==OperationSpec.CREW) {
            data.put("members",schedules.members(id));
            data.put("impacts",schedules.impacts(id));
            data.put("memberHistory",sql.selectMap("SELECT * FROM operation_crew_members_log WHERE crew_id=? ORDER BY log_id DESC",List.of(id)));
        }
        return data;
    }
    public Map<String,Object> lookup(Map<String,Object> p) {
        access.read(OperationSpec.VEHICLE);
        String type=Objects.toString(p.get("kind"),"");
        String code=required(p.get("code"),"コード",120);
        return switch(type) {
            case "employee" -> employee(code);
            case "vehicle" -> vehicle(code);
            case "qualification" -> unique("SELECT id,code,name,expiry_required,driver_license FROM operation_qualification_type WHERE code=? AND state=0",code);
            default -> throw new BusinessException("コードの種類が不正です。");
        };
    }
    public Map<String,Object> employee(String code) {
        return unique("""
            SELECT e.employee_id AS id,e.code,e.full_name AS name FROM employees e
            LEFT JOIN companies c ON c.company_id=e.company_id
            WHERE e.code=? AND e.state=0 AND e.category IN (1,2,3) AND (ISNULL(e.company_id,0)=0 OR c.state=0)
            """,code);
    }
    public Map<String,Object> vehicle(String code) {
        return unique("SELECT id,code,name,capacity FROM operation_vehicle WHERE code=? AND state=0",code);
    }
    private Map<String,Object> unique(String statement,String code) {
        var rows=sql.selectMap(statement,List.of(code));
        if(rows.size()!=1) throw new BusinessException(rows.isEmpty()?"コードに対応する有効な登録がありません。":"コードが重複しています。元の登録を確認してください。");
        return rows.get(0);
    }
    @Transactional
    public long save(Map<String,Object> p) {
        var spec=OperationSpec.from(p.get("entity")); access.write(spec,p);
        repo.schedulingLock();
        long key=p.get("id")==null?0:Long.parseLong(p.get("id").toString());
        Map<String,Object> old=key==0?Map.of():repo.get(spec,key,true);
        if(key!=0) OperationRepository.version(old,p.get("version"));
        if(Boolean.TRUE.equals(p.get("delete"))) {
            if(key==0) throw new BusinessException("削除するデータがありません。");
            checkDelete(spec,key);
            if(spec==OperationSpec.CREW) schedules.logMembers(key,((Number)old.get("version")).intValue()+1,"DELETE");
            return repo.save(spec,key,Map.of("state",9),access.editor());
        }
        var values=new LinkedHashMap<String,Object>();
        for(var f:spec.fields) {
            Object raw=p.containsKey(f.name())?p.get(f.name()):old.get(f.name());
            Object v=normalize(f,raw);
            values.put(OperationSpec.snake(f.name()),v);
        }
        Map<String,Object> vehicle=null;
        if(values.containsKey("vehicle_code")) {
            vehicle=vehicle(values.get("vehicle_code").toString()); values.put("vehicle_id",vehicle.get("id"));
        }
        if(values.containsKey("employee_code")) {
            var employee=employee(values.get("employee_code").toString());
            values.put("employee_id",employee.get("id")); values.put("employee_name",employee.get("name"));
        }
        if(spec==OperationSpec.VEHICLE) {
            positiveInteger(values.get("capacity"),"乗車定員");
            if(key!=0) {
                immutable(old.get("code"),values.get("code"),"登録済みの車両コード");
                var counts=sql.selectMap("SELECT c.id FROM operation_crew c JOIN operation_crew_members m ON m.crew_id=c.id WHERE c.vehicle_id=? AND c.state=0 AND c.work_date>=CAST(GETDATE() AS DATE) GROUP BY c.id HAVING COUNT(*)>?",List.of(key,values.get("capacity")));
                if(!counts.isEmpty()) throw new BusinessException("登録済み編成の人数より定員を減らせません。");
            }
            duplicate(spec,"code",values.get("code"),key);
        }
        if(spec==OperationSpec.QUALIFICATION_TYPE) {
            if(key!=0) immutable(old.get("code"),values.get("code"),"登録済みの資格コード");
            duplicate(spec,"code",values.get("code"),key);
        }
        if(spec==OperationSpec.QUALIFICATION) {
            var type=unique("SELECT id,code,name,expiry_required FROM operation_qualification_type WHERE code=? AND state=0",values.get("qualification_code").toString());
            values.put("qualification_type_id",type.get("id")); values.put("qualification_name",type.get("name"));
            if(Boolean.TRUE.equals(type.get("expiryRequired")) && values.get("expiry_date")==null) throw new BusinessException("この資格は有効期限の入力が必要です。");
            range(values,"acquired_date","expiry_date");
        }
        if(spec==OperationSpec.LABOR) {
            range(values,"effective_from","effective_to");
            for(String field:List.of("health_last_four","employment_last_four")) {
                Object v=values.get(field); if(v!=null && !v.toString().matches("[0-9]{1,4}")) throw new BusinessException("番号は末尾4桁以内の数字を入力してください。");
            }
            var overlap=sql.selectMap("SELECT id FROM operation_labor WHERE employee_id=? AND state=0 AND id<>? AND effective_from<=COALESCE(?,CONVERT(date,'99991231')) AND COALESCE(effective_to,CONVERT(date,'99991231'))>=?",Arrays.asList(values.get("employee_id"),key,values.get("effective_to"),values.get("effective_from")));
            if(!overlap.isEmpty()) throw new BusinessException("労務情報の適用期間が重複しています。前の期間を終了してから登録してください。");
        }
        if(spec==OperationSpec.HEALTH) {
            range(values,"exam_date","next_date");
            for(String f:List.of("systolic","diastolic")) if(values.get(f)!=null) positiveInteger(values.get(f),"血圧");
        }
        if(spec==OperationSpec.INSPECTION) {
            range(values,"unavailable_from","unavailable_to");
            if((values.get("unavailable_from")==null)!=(values.get("unavailable_to")==null)) throw new BusinessException("使用不可期間は開始日と終了日を入力してください。");
            if(values.get("due_date")==null && values.get("scheduled_date")==null && values.get("performed_date")==null) throw new BusinessException("期限・予定日・実施日のいずれかを入力してください。");
            schedules.checkMaintenance(values);
        }
        if(spec==OperationSpec.SCORE) {
            if(key!=0) for(String f:List.of("workDate","vehicleCode","employeeCode")) immutable(old.get(f),values.get(OperationSpec.snake(f)),"記録済みの運行日・車両・ドライバー");
            var exists=sql.selectMap("SELECT id FROM operation_score WHERE state=0 AND work_date=? AND vehicle_id=? AND employee_id=? AND id<>?",List.of(values.get("work_date"),values.get("vehicle_id"),values.get("employee_id"),key));
            if(!exists.isEmpty()) throw new BusinessException("同じ運行日・車両・ドライバーの得点が登録済みです。");
            if(sql.selectMap("SELECT m.employee_id FROM operation_crew c JOIN operation_crew_members m ON m.crew_id=c.id WHERE c.state=0 AND c.work_date=? AND c.vehicle_id=? AND m.employee_id=?",List.of(values.get("work_date"),values.get("vehicle_id"),values.get("employee_id"))).isEmpty())
                throw new BusinessException("その日の乗車メンバーに登録されていません。");
        }
        if(spec==OperationSpec.CREW) return schedules.saveCrew(key,old,values,p,vehicle);
        return repo.save(spec,key,values,access.editor());
    }
    private void checkDelete(OperationSpec spec,long key) {
        if(spec==OperationSpec.VEHICLE && !sql.selectMap("SELECT id FROM operation_crew WHERE vehicle_id=? AND state=0 AND work_date>=CAST(GETDATE() AS DATE)",List.of(key)).isEmpty()) throw new BusinessException("当日以降の編成がある車両は無効化できません。");
        if(spec==OperationSpec.QUALIFICATION_TYPE && !sql.selectMap("SELECT id FROM operation_qualification WHERE qualification_type_id=? AND state=0",List.of(key)).isEmpty()) throw new BusinessException("保有資格に使用中の資格種類は無効化できません。");
        if(spec==OperationSpec.CREW && !schedules.impacts(key).isEmpty()) throw new BusinessException("配車中の伝票があります。先に配車を解除してください。");
    }
    private void duplicate(OperationSpec spec,String column,Object value,long key) {
        if(!sql.selectMap("SELECT id FROM "+spec.table()+" WHERE "+column+"=? AND state=0 AND id<>?",List.of(value,key)).isEmpty()) throw new BusinessException("同じコードが登録されています。");
    }
    public static Object normalize(OperationSpec.Field f,Object raw) {
        if(f.type().equals("checkbox")) return Boolean.TRUE.equals(raw) || "true".equals(String.valueOf(raw));
        if(raw==null || raw.toString().isBlank()) { if(f.required()) throw new BusinessException(f.label()+"を入力してください。"); return null; }
        if(f.type().equals("date")) return date(raw,f.required());
        if(f.type().equals("number")) {
            try { var n=new BigDecimal(raw.toString()); if(n.signum()<0 || n.scale()>2 || n.compareTo(new BigDecimal("9999999999.99"))>0) throw new NumberFormatException(); return n; }
            catch(RuntimeException e) { throw new BusinessException(f.label()+"は0以上、小数2桁以内で入力してください。"); }
        }
        return required(raw,f.label(),f.name().equals("remarks")?1000:120);
    }
    public static long id(Object value) { try { long n=Long.parseLong(String.valueOf(value)); if(n>0) return n; } catch(RuntimeException ignored) {} throw new BusinessException("IDが不正です。"); }
    public static String required(Object value,String label,int max) { String s=Objects.toString(value,"").trim(); if(s.isBlank() || s.length()>max) throw new BusinessException(label+"を"+max+"文字以内で入力してください。"); return s; }
    public static Date date(Object value,boolean required) {
        if(!required && (value==null || value.toString().isBlank())) return null;
        try { return Date.valueOf(LocalDate.parse(value.toString().substring(0,10))); } catch(RuntimeException e) { throw new BusinessException("日付を正しく入力してください。"); }
    }
    public static void range(Map<String,Object> values,String from,String to) {
        if(values.get(from)!=null && values.get(to)!=null && ((Date)values.get(from)).after((Date)values.get(to))) throw new BusinessException("終了日は開始日以降にしてください。");
    }
    public static void immutable(Object a,Object b,String label) { if(!Objects.toString(a,"").equals(Objects.toString(b,""))) throw new BusinessException(label+"は変更できません。別の記録を作成してください。"); }
    public static int positiveInteger(Object value,String label) { try { int n=new BigDecimal(value.toString()).intValueExact(); if(n>0) return n; } catch(RuntimeException ignored) {} throw new BusinessException(label+"は1以上の整数で入力してください。"); }
}
