package com.kyouseipro.neo.domain.operations.dispatch.result;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;
import com.kyouseipro.neo.sql.repository.*;

class WorkResultServiceTest {
    final SqlRepository sql=mock(SqlRepository.class);
    final BaseSqlRepository base=mock(BaseSqlRepository.class);
    final OwnOfficeContextService context=mock(OwnOfficeContextService.class);
    final OrderHistoryRepository history=mock(OrderHistoryRepository.class);
    final WorkResultService service=new WorkResultService(sql,base,context,history);
    final UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(
        "employee","unused",List.of(new SimpleGrantedAuthority("APPROLE_user")));
    Map<String,Object> row;
    List<Map<String,Object>> duplicates=List.of();
    @BeforeEach void setup() {
        row=new HashMap<>();row.put("dailyCrewId",8);row.put("assignmentVersion",3);
        row.put("workDate", "2026-10-31");row.put("workResultId",null);row.put("resultVersion",null);
        when(context.getEmployeeId(auth)).thenReturn(42L);
        when(context.getAccount(auth)).thenReturn("employee");
        when(sql.selectMap(anyString(), anyList())).thenAnswer(call -> {
            String query=call.getArgument(0);
            if(query.contains("AS assignment_version")) return List.of(new HashMap<>(row));
            if(query.contains("FROM dbo.order_work_results_log")) return duplicates;
            return List.of(Map.of("dailyCrewId",8));
        });
        when(base.insert(eq(WorkResultService.TABLE),anyMap(),anyString())).thenReturn(90L);
    }
    WorkResultService.Request req(String type,String reason,LocalDate date,Integer resultVersion) {
        return new WorkResultService.Request(3,resultVersion,type,reason,date == null ? LocalDate.of(2026,10,31) : date,UUID.randomUUID());
    }
    @Test void completionUsesServerEmployeeAndRecordsAfterCommonSave() {
        service.save(auth,100,12,req("COMPLETED","",null,null));
        var sequence=inOrder(base,history);
        sequence.verify(base).insert(eq(WorkResultService.TABLE),argThat(p -> p.get("resultEmployeeId").equals(42L)
            && p.get("dispatchAssignmentId").equals(12L) && !p.containsKey("orderId")),eq("employee"));
        sequence.verify(history).recordWorkResult(90L,false,"employee");
    }
    @Test void failureAndPostponementRequireReasonsAndValidateDate() {
        for(String type:List.of("DELIVERY_FAILED","POSTPONED"))
            assertThrows(BusinessException.class,()->service.save(auth,100,12,req(type," ",null,null)));
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("POSTPONED","都合",LocalDate.of(2026,11,2),null)));
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("COMPLETED","補足",LocalDate.of(2026,11,1),null)));
        verifyNoInteractions(base,history);
    }
    @Test void postponementOnlySavesReportWithNoOrderOrDispatchUpdate() {
        service.save(auth,100,12,req("POSTPONED","希望日は事務側で調整",null,null));
        verify(base).insert(eq(WorkResultService.TABLE),argThat(p->p.get("workDate").toString().equals("2026-10-31") && !p.containsKey("nextVisitDate")),anyString());
        verifyNoMoreInteractions(base);
    }
    @Test void correctionRequiresReasonAndChecksResultVersion() {
        row.put("workResultId",90L);row.put("resultVersion",1);
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("COMPLETED","",null,1)));
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("COMPLETED","訂正",null,0)));
        service.save(auth,100,12,req("COMPLETED","誤報告を訂正",null,1));
        verify(base).update(eq(WorkResultService.TABLE),argThat(p->p.get("version").equals(1)),anyString());
        verify(history).recordWorkResult(90L,true,"employee");
    }
    @Test void staleAssignmentUnknownTypeAndMissingUserAreRejected() {
        row.put("assignmentVersion",4);
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("COMPLETED","",null,null)));
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("OTHER","",null,null)));
        assertThrows(AccessDeniedException.class,()->service.save(null,100,12,req("COMPLETED","",null,null)));
        verifyNoInteractions(base,history);
    }
    @Test void onlyAssignedEmployeeCanReadOrSave() {
        when(sql.selectMap(contains("AS assignment_version"),anyList())).thenReturn(List.of());
        assertThrows(AccessDeniedException.class,()->service.detail(auth,100,12));
        assertThrows(AccessDeniedException.class,()->service.save(auth,100,12,req("COMPLETED","",null,null)));
        verifyNoInteractions(base,history);
    }
    @Test void idempotentReplayDoesNotWriteEvenAfterLaterCorrectionButChangedPayloadFails() {
        duplicates=List.of(Map.of("dispatchAssignmentId",12,"workDate","2026-10-31","resultType","COMPLETED","reason",""));
        row.put("resultVersion",8);
        service.save(auth,100,12,req("COMPLETED","",null,null));
        verifyNoInteractions(base,history);
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("DELIVERY_FAILED","変更",null,null)));
    }
    @Test void changingCrewDuringLockAndHistoryFailureAreNotReportedAsSuccess() {
        when(sql.selectMap(contains("AS assignment_version"),anyList())).thenReturn(List.of(new HashMap<>(row)),List.of(Map.of("dailyCrewId",9)));
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("COMPLETED","",null,null)));
        verifyNoInteractions(base,history);
        when(sql.selectMap(contains("AS assignment_version"),anyList())).thenAnswer(call -> List.of(new HashMap<>(row)));
        doThrow(new BusinessException("history failed")).when(history).recordWorkResult(anyLong(),anyBoolean(),anyString());
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("COMPLETED","",null,null)));
    }

    @Test void visitDateTokenRejectsAnOldScreenAfterTheCrewMovesToAnotherDay() {
        row.put("workDate","2026-11-02");
        assertThrows(BusinessException.class,()->service.save(auth,100,12,req("COMPLETED","",null,null)));
        verifyNoInteractions(base,history);
        service.detail(auth,100,12);
        verify(sql,atLeastOnce()).selectMap(argThat(q->q.contains("wr.work_date=dc.work_date")),anyList());
    }
}
