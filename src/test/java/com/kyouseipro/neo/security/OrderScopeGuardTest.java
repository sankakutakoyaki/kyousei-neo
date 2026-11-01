package com.kyouseipro.neo.security;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
class OrderScopeGuardTest {
    final OrderAccessService access=mock(OrderAccessService.class);final PermissionService permissions=mock(PermissionService.class);
    final SqlRepository sql=mock(SqlRepository.class);final Authentication auth=mock(Authentication.class);
    final com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService context=mock(com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService.class);
    final OrderScopeGuard guard=new OrderScopeGuard(access,permissions,sql,context);
    @Test void officeWritesRequireBothTableAndOperation(){
        when(permissions.canTable(auth,"orders","update")).thenReturn(true);
        assertThrows(AccessDeniedException.class,()->guard.require(auth,10,true));verifyNoInteractions(sql);
    }
    @Test void assignedScopeUsesServerEmployeeAndDeniesUnrelatedOrder(){
        when(permissions.canTable(auth,"orders","select")).thenReturn(true);
        when(access.getAccessCondition(auth)).thenReturn(new OrderAccessService.AccessCondition("ASSIGNED",null,42L));
        when(sql.selectMap(anyString(),anyList())).thenReturn(List.of());
        assertThrows(AccessDeniedException.class,()->guard.require(auth,99,false));
        verify(sql).selectMap(contains("dcm.employee_id=?"),argThat(p->p.get(5).equals(42L)));
    }
    @Test void driverWithoutOfficePermissionsIsRestrictedToServerEmployee() {
        when(auth.isAuthenticated()).thenReturn(true);
        doReturn(List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("APPROLE_leader"))).when(auth).getAuthorities();
        when(context.getEmployeeId(auth)).thenReturn(42L);
        var scope=guard.readCondition(auth);assertEquals("ASSIGNED",scope.scope());assertEquals(42L,scope.employeeId());
        assertThrows(AccessDeniedException.class,()->guard.require(auth,1,true));
    }

}
