package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;

class OrderAccessServiceTest {

    private DataScopeService dataScopeService;
    private OwnOfficeContextService ownOfficeContextService;
    private OrderAccessService service;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        dataScopeService = mock(DataScopeService.class);
        ownOfficeContextService = mock(OwnOfficeContextService.class);
        authentication = mock(Authentication.class);

        service = new OrderAccessService(
            dataScopeService,
            ownOfficeContextService
        );
    }

    @Test
    void allScope() {
        when(dataScopeService.getScope(authentication)).thenReturn("ALL");

        var result = service.getAccessCondition(authentication);

        assertEquals("ALL", result.scope());
        assertNull(result.officeId());
        assertNull(result.employeeId());
    }

    @Test
    void ownScope() {
        when(dataScopeService.getScope(authentication)).thenReturn("OWN");
        when(ownOfficeContextService.getOfficeId(authentication))
            .thenReturn(1001);

        var result = service.getAccessCondition(authentication);

        assertEquals("OWN", result.scope());
        assertEquals(1001, result.officeId());
        assertNull(result.employeeId());
    }

    @Test
    void assignedScope() {
        when(dataScopeService.getScope(authentication)).thenReturn("ASSIGNED");
        when(ownOfficeContextService.getEmployeeId(authentication))
            .thenReturn(2001L);

        var result = service.getAccessCondition(authentication);

        assertEquals("ASSIGNED", result.scope());
        assertNull(result.officeId());
        assertEquals(2001L, result.employeeId());
    }

    @Test
    void ownAssignedScope() {
        when(dataScopeService.getScope(authentication))
            .thenReturn("OWN_ASSIGNED");
        when(ownOfficeContextService.getOfficeId(authentication))
            .thenReturn(1001);
        when(ownOfficeContextService.getEmployeeId(authentication))
            .thenReturn(2001L);

        var result = service.getAccessCondition(authentication);

        assertEquals("OWN_ASSIGNED", result.scope());
        assertEquals(1001, result.officeId());
        assertEquals(2001L, result.employeeId());
    }

    @Test
    void ownWithoutOfficeIsDenied() {
        when(dataScopeService.getScope(authentication)).thenReturn("OWN");
        when(ownOfficeContextService.getOfficeId(authentication))
            .thenReturn((Integer) null);

        assertThrows(AccessDeniedException.class,
            () -> service.getAccessCondition(authentication));
    }

    @Test
    void assignedWithoutEmployeeIsDenied() {
        when(dataScopeService.getScope(authentication)).thenReturn("ASSIGNED");
        when(ownOfficeContextService.getEmployeeId(authentication))
            .thenReturn((Long) null);

        assertThrows(AccessDeniedException.class,
            () -> service.getAccessCondition(authentication));
    }

    @Test
    void unknownScopeIsDenied() {
        when(dataScopeService.getScope(authentication)).thenReturn(null);

        assertThrows(AccessDeniedException.class,
            () -> service.getAccessCondition(authentication));
    }
}