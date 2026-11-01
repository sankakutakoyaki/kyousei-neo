package com.kyouseipro.neo.domain.business.order.outcome;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import java.time.LocalDate;
import org.junit.jupiter.api.*;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.*;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository;
class OrderOutcomeServiceTest {
    final SqlRepository sql=mock(SqlRepository.class);final BaseSqlRepository base=mock(BaseSqlRepository.class);
    final OrderHistoryRepository history=mock(OrderHistoryRepository.class);
    final OrderOutcomeService service=new OrderOutcomeService(sql,base,history);
    final LocalDate day=LocalDate.of(2026,10,18);Map<String,Object> row;
    @BeforeEach void setup(){row=new HashMap<>(Map.of("version",3,"orderStatus","ACTIVE","visitDate",java.sql.Date.valueOf(day)));
        when(sql.selectMap(contains("SYSDATETIME"),anyList())).thenReturn(List.of(Map.of("reportedAt",java.sql.Timestamp.valueOf("2026-11-01 15:00:00"))));
        when(sql.selectMap(anyString(),anyList())).thenAnswer(call->call.<String>getArgument(0).contains("SYSDATETIME")?List.of(Map.of("reportedAt",java.sql.Timestamp.valueOf("2026-11-01 15:00:00"))):call.<String>getArgument(0).contains("orders_log")?List.of():List.of(row));}
    @Test void absenceUsesDatabaseClockAndPreservesVisitPlan(){
        service.record(1L,3,"ABSENT","",null,day,UUID.randomUUID(),"reporter");
        verify(base).update(eq(Tables.ORDER_BY_IDS),argThat(data->"ABSENT".equals(data.get("orderStatus"))
            && data.get("outcomeReportedAt").equals(java.sql.Timestamp.valueOf("2026-11-01 15:00:00"))
            && !data.containsKey("visitDate") && !data.containsKey("visitTime")),eq("reporter"));
    }
    @Test void exceptionsClearOnlyNextVisitAndKeepLoggedReason(){
        for(String type:List.of("DELIVERY_FAILED","POSTPONED")) {
            service.record(1L,3,type,"現地の理由",null,day,UUID.randomUUID(),"reporter");
            verify(base).update(eq(Tables.ORDER_BY_IDS),argThat(data->type.equals(data.get("outcomeType"))
                && "NEEDS_ACTION".equals(data.get("orderStatus")) && data.containsKey("visitDate") && data.get("visitDate")==null
                && data.get("lastWorkDate").toString().equals("2026-10-18")),eq("reporter"));
        }
        verify(history,times(2)).recordSaved(1L,false,"reporter");
    }
    @Test void staleDateVersionAndCancelledAreRejected(){
        assertThrows(BusinessException.class,()->service.record(1L,2,"COMPLETED","",null,day,UUID.randomUUID(),"x"));
        assertThrows(BusinessException.class,()->service.record(1L,3,"POSTPONED","理由",null,day.plusDays(1),UUID.randomUUID(),"x"));
        row.put("orderStatus","CANCELLED");
        assertThrows(BusinessException.class,()->service.record(1L,3,"COMPLETED","",null,day,UUID.randomUUID(),"x"));
        verifyNoInteractions(base,history);
    }
    @Test void cancellationUsesDedicatedStatusNotDeleteAndRequiresReason(){
        assertThrows(BusinessException.class,()->service.record(1L,3,"CANCELLED","", "OTHER",null,UUID.randomUUID(),"x"));
        service.record(1L,3,"CANCELLED","搬入口が狭い","DELIVERY_FAILED",day,UUID.randomUUID(),"x");
        verify(base).update(eq(Tables.ORDER_BY_IDS),argThat(data->"CANCELLED".equals(data.get("orderStatus")) && !data.containsKey("state")),eq("x"));
    }
    @Test void idempotentReplayDoesNotOverwriteLaterChanges(){
        when(sql.selectMap(contains("orders_log"),anyList())).thenReturn(List.of(Map.of("outcomeType","POSTPONED","outcomeReason","理由","lastWorkDate",java.sql.Date.valueOf(day))));
        row.put("version",9);service.record(1L,3,"POSTPONED","理由",null,day,UUID.randomUUID(),"x");
        verifyNoInteractions(base,history);
    }
}
