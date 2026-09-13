package com.kyouseipro.neo.domain.business.dispatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import jakarta.servlet.http.HttpServletRequest;

class DispatchServiceTest {
    final SqlRepository sql = mock(SqlRepository.class);
    final HttpServletRequest request = mock(HttpServletRequest.class);
    final DispatchService service = new DispatchService(sql, new DispatchLogSqlProvider(), request);
    Map<String,Object> params() {
        return new HashMap<>(Map.of("orderId",1,"version",2,"dispatchVersion",0,"mobile",false,
            "assignments", List.of(Map.of("employeeId",10,"role","DELIVERY"), Map.of("employeeId",10,"role","INSTALL"))));
    }
    void auth(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("verified-user", "", List.of(new SimpleGrantedAuthority(role))));
    }
    @BeforeEach void setup() {
        auth("APPROLE_user");
        when(sql.selectMap(contains("FROM orders WITH"), anyList())).thenReturn(List.of(Map.of("orderId",1,"version",2,"state",0)));
        when(sql.selectMap(contains("FROM employees e"), anyList())).thenReturn(List.of(Map.of("employeeId",10,"fullName","担当者")));
    }
    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }
    @Test void createsBothRolesAndUsesAuthenticatedEditor() {
        var p = params(); p.put("editor", "spoofed"); service.save(p);
        verify(sql, times(2)).update(contains("INSERT order_dispatch_assignments"), argThat((List<Object> a) -> a.get(4).equals("verified-user")));
        verify(sql).update(contains("INSERT order_dispatch_versions"), anyList());
    }
    @Test void mutationsIncludeLogInSameSqlBatchWithAuthenticatedEditorAndRevision() {
        when(sql.selectMap(contains("SELECT employee_id, role"), anyList())).thenReturn(List.of(Map.of("employeeId",11,"role","DELIVERY")));
        service.save(params());
        var statements = org.mockito.ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        var values = (org.mockito.ArgumentCaptor<List<Object>>) (org.mockito.ArgumentCaptor<?>) org.mockito.ArgumentCaptor.forClass(List.class);
        verify(sql, times(4)).update(statements.capture(), values.capture());
        for (int i=0; i<3; i++) {
            String statement = statements.getAllValues().get(i);
            var args = values.getAllValues().get(i);
            assertTrue(statement.contains("OUTPUT INSERTED.assignment_id"));
            assertTrue(statement.contains("INTO @DispatchRows"));
            assertTrue(statement.contains("INSERT INTO order_dispatch_assignments_log"));
            assertEquals("verified-user", args.get(args.size()-3));
            assertEquals(i == 0 ? "UPDATE" : "INSERT", args.get(args.size()-2));
            assertEquals(1, args.get(args.size()-1));
        }
    }
    @Test void unchangedAssignmentsDoNotGenerateLogsOrIncrementVersion() {
        when(sql.selectMap(contains("SELECT employee_id, role"), anyList())).thenReturn(List.of(
            Map.of("employeeId",10,"role","DELIVERY"), Map.of("employeeId",10,"role","INSTALL")));
        service.save(params());
        verify(sql, never()).update(anyString(), anyList());
    }
    @Test void rejectsStaleOrderAndStaleDispatchWithoutWrites() {
        var p = params(); p.put("version",1);
        assertThrows(BusinessException.class, () -> service.save(p));
        p.put("version",2); p.put("dispatchVersion",1);
        assertThrows(BusinessException.class, () -> service.save(p));
        verify(sql, never()).update(anyString(), anyList());
    }
    @Test void rejectsMissingDeletedCompletedOrdersAndUnavailableEmployees() {
        when(sql.selectMap(contains("FROM employees e"), anyList())).thenReturn(List.of());
        assertThrows(BusinessException.class, () -> service.save(params()));
        when(sql.selectMap(contains("FROM orders WITH"), anyList())).thenReturn(List.of(Map.of("version",2,"state",2)));
        assertThrows(BusinessException.class, () -> service.save(params()));
        when(sql.selectMap(contains("FROM orders WITH"), anyList())).thenReturn(List.of());
        assertThrows(BusinessException.class, () -> service.save(params()));
        verify(sql, never()).update(anyString(), anyList());
    }
    @Test void removingAssignmentRetainsAuditAndIncrementsVersion() {
        when(sql.selectMap(contains("SELECT employee_id, role"), anyList())).thenReturn(List.of(Map.of("employeeId",10,"role","DELIVERY")));
        when(sql.selectMap(contains("SELECT version FROM order_dispatch_versions"), anyList())).thenReturn(List.of(Map.of("version",3)));
        var p=params(); p.put("assignments",List.of()); p.put("dispatchVersion",3); service.save(p);
        verify(sql).update(contains("cancelled_at=SYSDATETIME()"), anyList());
        verify(sql).update(contains("SET version=version+1"), anyList());
        verify(sql, never()).update(startsWith("DELETE"), anyList());
    }
    @Test void ordinaryMobileCannotSaveAndServerDetectsMobileHeaders() {
        var p=params(); p.put("mobile",true);
        assertThrows(AccessDeniedException.class, () -> service.save(p));
        p.put("mobile",false); when(request.getHeader("User-Agent")).thenReturn("Mozilla iPhone");
        assertThrows(AccessDeniedException.class, () -> service.save(p));
        verify(sql, never()).update(anyString(), anyList());
    }
    @Test void managementRolesCanSaveFromMobile() {
        for (String role : List.of("APPROLE_admin","APPROLE_master","APPROLE_leader")) {
            auth(role); var p=params(); p.put("mobile",true); assertDoesNotThrow(() -> service.save(p));
        }
    }
    @Test void rejectsDuplicatesInvalidRolesAndIds() {
        for (Object value : List.of(List.of(Map.of("role","OTHER","employeeId",10)),
                List.of(Map.of("role","DELIVERY","employeeId",-1)),
                List.of(Map.of("role","DELIVERY","employeeId",10),Map.of("role","DELIVERY","employeeId",10))))
            assertThrows(BusinessException.class, () -> DispatchService.validateAssignments(value));
    }
    @Test void searchValidatesRangeAndBindsDates() {
        assertThrows(BusinessException.class, () -> service.list(Map.of("dateFrom","2026-09-13","dateTo","2026-09-12")));
        service.list(Map.of("dateFrom","2026-09-13","dateTo","2026-09-13","includeUndated",true));
        verify(sql).selectMap(contains("OR o.visit_date IS NULL"), argThat((List<Object> p) -> p.get(1).toString().equals("2026-09-14")));
    }
}
