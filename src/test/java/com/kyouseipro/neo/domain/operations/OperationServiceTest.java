package com.kyouseipro.neo.domain.operations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;

class OperationServiceTest {
 final SqlRepository sql=mock(SqlRepository.class);
 final OperationRepository repo=mock(OperationRepository.class);
 final OperationScheduleService schedule=mock(OperationScheduleService.class);
 final HttpServletRequest request=mock(HttpServletRequest.class);
 final OperationAccess access=new OperationAccess(request);
 final OperationService service=new OperationService(sql,repo,access,schedule);
 @BeforeEach void setup(){role("APPROLE_admin");}
 @AfterEach void cleanup(){SecurityContextHolder.clearContext();}
 void role(String role){SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("real-editor","",List.of(new SimpleGrantedAuthority(role))));}
 Map<String,Object> base(String kind){return new HashMap<>(Map.of("entity",kind,"mobile",false));}
 @Test void sensitiveInformationAndCertificateDetailsAreNotAvailableToLeader(){
  role("APPROLE_leader");
  for(String kind:List.of("LABOR","HEALTH")) assertThrows(AccessDeniedException.class,()->service.list(Map.of("entity",kind)));
  assertThrows(AccessDeniedException.class,()->service.detail(Map.of("entity","QUALIFICATION","id",1)));
  assertThrows(AccessDeniedException.class,()->service.save(base("QUALIFICATION_TYPE")));
  verifyNoInteractions(repo);
 }
 @Test void qualificationSummaryDoesNotSelectCertificateNumberOrRemarks(){
  role("APPROLE_leader");service.list(Map.of("entity","QUALIFICATION"));
  verify(sql).selectMap(argThat(s->s.contains("qualification_name")&&!s.contains("certificate_number")&&!s.contains("remarks")&&!s.contains("SELECT *")),anyList());
 }
 @Test void vehicleSaveWhitelistsFieldsAndChecksCapacity(){
  var p=base("VEHICLE");p.putAll(Map.of("code","V01","name","トラック","plateNumber","1234","vehicleType","トラック","capacity","3","editor","spoofed","state",9));
  service.save(p);
  verify(repo).save(eq(OperationSpec.VEHICLE),eq(0L),argThat(v->!v.containsKey("editor")&&!v.containsKey("state")&&v.get("capacity").toString().equals("3")),eq("real-editor"));
  p.put("capacity","2.5");assertThrows(BusinessException.class,()->service.save(p));
 }
 @Test void staleUpdatesCannotWrite(){
  when(repo.get(OperationSpec.VEHICLE,1,true)).thenReturn(Map.of("version",3));
  var p=base("VEHICLE");p.putAll(Map.of("id",1,"version",2));
  assertThrows(BusinessException.class,()->service.save(p));verify(repo,never()).save(any(),anyLong(),anyMap(),anyString());
 }
 @Test void codeLookupRejectsAmbiguousEmployeeCodes(){
  when(sql.selectMap(contains("e.code=?"),anyList())).thenReturn(List.of(Map.of("id",1),Map.of("id",2)));
  assertThrows(BusinessException.class,()->service.lookup(Map.of("kind","employee","code","100")));
 }
 @Test void laborPeriodOverlapAndFullInsuranceNumbersAreRejected(){
  when(sql.selectMap(contains("e.code=?"),anyList())).thenReturn(List.of(Map.of("id",1,"name","作業員")));
  var p=base("LABOR");p.putAll(Map.of("employeeCode","100","effectiveFrom","2026-09-01","healthLastFour","123456"));
  assertThrows(BusinessException.class,()->service.save(p));p.put("healthLastFour","1234");
  when(sql.selectMap(contains("FROM operation_labor"),anyList())).thenReturn(List.of(Map.of("id",9)));
  assertThrows(BusinessException.class,()->service.save(p));verify(repo,never()).save(any(),anyLong(),anyMap(),anyString());
 }
 @Test void requiredExpiryCannotBeBlank(){
  when(sql.selectMap(contains("e.code=?"),anyList())).thenReturn(List.of(Map.of("id",1,"name","作業員")));
  when(sql.selectMap(contains("FROM operation_qualification_type"),anyList())).thenReturn(List.of(Map.of("id",2,"name","免許","expiryRequired",true)));
  var p=base("QUALIFICATION");p.putAll(Map.of("employeeCode","100","qualificationCode","L1","acquiredDate","2026-01-01"));
  assertThrows(BusinessException.class,()->service.save(p));
 }
 @Test void missingScoreRemainsNullAndZeroRemainsZero(){
  var field=new OperationSpec.Field("score","得点","number",false);
  assertNull(OperationService.normalize(field,""));assertEquals("0",OperationService.normalize(field,"0").toString());
  assertThrows(BusinessException.class,()->OperationService.normalize(field,"-1"));
 }
 @Test void scoreIdentityCannotBeChangedByCrewChangesOrEdits(){
  when(repo.get(OperationSpec.SCORE,1,true)).thenReturn(Map.of("version",0,"workDate","2026-09-01","vehicleCode","V1","employeeCode","100"));
  when(sql.selectMap(contains("FROM operation_vehicle"),anyList())).thenReturn(List.of(Map.of("id",1,"name","車両")));
  when(sql.selectMap(contains("e.code=?"),anyList())).thenReturn(List.of(Map.of("id",2,"name","別人")));
  var p=base("SCORE");p.putAll(Map.of("id",1,"version",0,"employeeCode","101"));
  assertThrows(BusinessException.class,()->service.save(p));
 }
 @Test void mobileGeneralUserCannotEditCrewEvenWhenClaimingDesktop(){
  role("APPROLE_user");when(request.getHeader("User-Agent")).thenReturn("Mozilla iPhone");
  assertThrows(AccessDeniedException.class,()->service.save(base("CREW")));
 }
}
