package com.kyouseipro.neo.domain.operations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import java.sql.Date;
import org.junit.jupiter.api.*;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.common.exception.BusinessException;

class OperationScheduleTest {
 final SqlRepository sql=mock(SqlRepository.class);
 final OperationRepository repo=mock(OperationRepository.class);
 final OperationAccess access=mock(OperationAccess.class);
 final OperationScheduleService service=new OperationScheduleService(sql,repo,access);
 @BeforeEach void setup(){
  when(access.editor()).thenReturn("editor");
  when(sql.selectMap(contains("e.code=?"),anyList())).thenAnswer(inv->{String code=inv.<List<Object>>getArgument(1).get(0).toString();return List.of(Map.of("employeeId",Integer.valueOf(code),"employeeCode",code,"employeeName","担当"+code));});
  when(repo.save(eq(OperationSpec.CREW),anyLong(),anyMap(),anyString())).thenReturn(7L);
 }
 Map<String,Object> values(){return Map.of("work_date",Date.valueOf("2026-09-14"),"vehicle_code","V1","vehicle_id",1);}
 List<Map<String,Object>> people(){return List.of(Map.of("employeeCode","100","isDriver",true),Map.of("employeeCode","101","isDriver",false),Map.of("employeeCode","102","isDriver",false));}
 @Test void supportsThreeOrMoreMembersAndLogsBeforeReplacing(){
  assertEquals(7,service.saveCrew(0,Map.of(),values(),Map.of("members",people()),Map.of("id",1,"capacity",5)));
  verify(sql,times(3)).update(startsWith("INSERT operation_crew_members("),anyList());
  verify(sql).update(contains("INSERT operation_crew_members_log"),anyList());
 }
 @Test void rejectsOverCapacityMissingDriverAndDuplicateMembers(){
  assertThrows(BusinessException.class,()->service.saveCrew(0,Map.of(),values(),Map.of("members",people()),Map.of("id",1,"capacity",2)));
  assertThrows(BusinessException.class,()->service.saveCrew(0,Map.of(),values(),Map.of("members",List.of(Map.of("employeeCode","100","isDriver",false))),Map.of("id",1,"capacity",5)));
  assertThrows(BusinessException.class,()->service.saveCrew(0,Map.of(),values(),Map.of("members",List.of(people().get(0),people().get(0))),Map.of("id",1,"capacity",5)));
  verify(repo,never()).save(any(),anyLong(),anyMap(),anyString());
 }
 @Test void rejectsDoubleBookingAndUnavailableVehicle(){
  when(sql.selectMap(contains("c.id<>? AND m.employee_id=?"),anyList())).thenReturn(List.of(Map.of("id",2)));
  assertThrows(BusinessException.class,()->service.saveCrew(0,Map.of(),values(),Map.of("members",people()),Map.of("id",1,"capacity",5)));
  when(sql.selectMap(contains("c.id<>? AND m.employee_id=?"),anyList())).thenReturn(List.of());
  when(sql.selectMap(contains("FROM operation_inspection"),anyList())).thenReturn(List.of(Map.of("id",2)));
  assertThrows(BusinessException.class,()->service.saveCrew(0,Map.of(),values(),Map.of("members",people()),Map.of("id",1,"capacity",5)));
 }
 @Test void crewChangesRequireExactImpactVersions(){
  when(sql.selectMap(contains("p.leader_id"),anyList())).thenReturn(List.of(Map.of("orderId",20,"version",3,"orderVersion",4,"requestNumber","A")));
  assertThrows(BusinessException.class,()->service.saveCrew(7,Map.of("workDate","2026-09-14","vehicleCode","V1","version",0),values(),Map.of("members",people(),"impacts",List.of()),Map.of("id",1,"capacity",5)));
  verify(repo,never()).save(any(),anyLong(),anyMap(),anyString());
 }
 @Test void impactTokensIncludeOrderAndPlanVersion(){
  assertNotEquals(OperationScheduleService.impactTokens(List.of(Map.of("orderId",1,"version",1,"orderVersion",2))),OperationScheduleService.impactTokens(List.of(Map.of("orderId",1,"version",1,"orderVersion",3))));
 }
 @Test void completedOrdersCannotBeReassigned(){
  when(sql.selectMap(contains("COALESCE(p.version,0)"),anyList())).thenReturn(List.of(Map.of("orderId",1,"version",2,"orderVersion",3,"state",2)));
  assertThrows(BusinessException.class,()->service.dispatchSave(Map.of("orderId",1,"version",2,"orderVersion",3,"mobile",false,"vehicles",List.of())));
  verify(sql,never()).update(startsWith("DELETE"),anyList());
 }
 @Test void responsibilityMustBelongToChosenVehicles(){
  when(sql.selectMap(contains("COALESCE(p.version,0)"),anyList())).thenReturn(List.of(Map.of("orderId",1,"version",0,"orderVersion",3,"state",0,"visitDate",Date.valueOf("2026-09-14"))));
  when(repo.get(OperationSpec.CREW,7,true)).thenReturn(Map.of("id",7,"version",0,"vehicleId",1,"vehicleCode","V1","workDate",Date.valueOf("2026-09-14")));
  when(repo.get(OperationSpec.VEHICLE,1,true)).thenReturn(Map.of("id",1,"name","車両","capacity",3));
  when(sql.selectMap(contains("SELECT employee_id,employee_code"),anyList())).thenReturn(List.of(Map.of("employeeId",100,"employeeName","担当100")));
  when(sql.selectMap(contains("e.employee_id=?"),anyList())).thenReturn(List.of(Map.of("employeeId",100)));
  assertThrows(BusinessException.class,()->service.dispatchSave(Map.of("orderId",1,"version",0,"orderVersion",3,"mobile",false,"vehicles",List.of(Map.of("crewId",7,"version",0)),"leaderId",999)));
  verify(sql,never()).update(startsWith("INSERT operation_order_plans"),anyList());
 }
 @Test void multipleVehiclesSaveMemberSnapshotsAndAuditTogether(){
  when(sql.selectMap(contains("COALESCE(p.version,0)"),anyList())).thenReturn(List.of(Map.of("orderId",1,"version",0,"orderVersion",3,"state",0,"visitDate",Date.valueOf("2026-09-14"))));
  for(long crew:List.of(7L,8L)) {
   when(repo.get(OperationSpec.CREW,crew,true)).thenReturn(Map.of("id",crew,"version",0,"vehicleId",crew,"vehicleCode","V"+crew,"workDate",Date.valueOf("2026-09-14")));
   when(repo.get(OperationSpec.VEHICLE,crew,true)).thenReturn(Map.of("id",crew,"name","車両"+crew,"capacity",3));
  }
  when(sql.selectMap(contains("SELECT employee_id,employee_code"),anyList())).thenAnswer(inv->{long crew=((Number)inv.<List<Object>>getArgument(1).get(0)).longValue();return List.of(Map.of("employeeId",crew+100,"employeeName","担当"+crew,"isDriver",true));});
  when(sql.selectMap(contains("e.employee_id=?"),anyList())).thenReturn(List.of(Map.of("employeeId",107)));
  assertEquals(1,service.dispatchSave(Map.of("orderId",1,"version",0,"orderVersion",3,"mobile",false,"vehicles",List.of(Map.of("crewId",7,"version",0),Map.of("crewId",8,"version",0)),"leaderId",107)));
  verify(sql,times(2)).update(startsWith("INSERT operation_order_vehicles("),anyList());
  verify(sql,times(2)).update(startsWith("INSERT operation_order_members("),anyList());
  verify(sql).update(contains("INSERT operation_order_plans_log"),eq(List.of("editor","UPDATE",1L)));
 }
}
