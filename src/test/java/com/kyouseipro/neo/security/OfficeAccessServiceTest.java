package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;

class OfficeAccessServiceTest {

    private OwnOfficeContextService ownOfficeContextService;
    private DataScopeService dataScopeService;
    private OfficeAccessService service;

    @BeforeEach
    void setUp() {
        ownOfficeContextService = mock(OwnOfficeContextService.class);
        dataScopeService = mock(DataScopeService.class);
        service = new OfficeAccessService(ownOfficeContextService, dataScopeService);
    }

    private Authentication login(String role) {
        var auth = new TestingAuthenticationToken(
            "test-user", "password", role
        );
        auth.setAuthenticated(true);
        return auth;
    }

    @Test
    void masterCanAccessAllOffices() {
        Authentication auth = login("APPROLE_master");
        when(dataScopeService.getScope(auth)).thenReturn("ALL");

        assertTrue(service.canAccessAllOffices(auth));
        assertTrue(service.canAccessOffice(auth, 1001));
        assertTrue(service.canAccessOffice(auth, 1002));
        assertNull(service.getRestrictedOfficeId(auth));

        verifyNoInteractions(ownOfficeContextService);
    }

    @Test
    void masterCanBeRestrictedToOwnOffice() {
        Authentication auth = login("APPROLE_master");
        when(dataScopeService.getScope(auth)).thenReturn("OWN");
        when(ownOfficeContextService.getOfficeId(auth)).thenReturn(1001);

        assertFalse(service.canAccessAllOffices(auth));
        assertTrue(service.canAccessOffice(auth, 1001));
        assertFalse(service.canAccessOffice(auth, 1002));
        assertEquals(1001, service.getRestrictedOfficeId(auth));
    }

    @Test
    void officeCanAccessOnlyOwnOffice() {
        Authentication auth = login("APPROLE_office");
        when(dataScopeService.getScope(auth)).thenReturn("OWN");
        when(ownOfficeContextService.getOfficeId(auth)).thenReturn(1001);

        assertTrue(service.canAccessOffice(auth, 1001));
        assertFalse(service.canAccessOffice(auth, 1002));
        assertEquals(1001, service.getRestrictedOfficeId(auth));
    }

    @Test
    void userCanAccessOnlyOwnOffice() {
        Authentication auth = login("APPROLE_user");
        when(dataScopeService.getScope(auth)).thenReturn("OWN_ASSIGNED");
        when(ownOfficeContextService.getOfficeId(auth)).thenReturn(1002);

        assertTrue(service.canAccessOffice(auth, 1002));
        assertFalse(service.canAccessOffice(auth, 1001));
        assertEquals(1002, service.getRestrictedOfficeId(auth));
    }

    @Test
    void assignedScopeDoesNotGrantOfficeAccess() {
        Authentication auth = login("APPROLE_user");
        when(dataScopeService.getScope(auth)).thenReturn("ASSIGNED");

        assertFalse(service.canAccessAllOffices(auth));
        assertFalse(service.canAccessOffice(auth, 1001));

        assertThrows(
            AccessDeniedException.class,
            () -> service.getRestrictedOfficeId(auth)
        );
    }

    @Test
    void unknownOfficeIsDenied() {
        Authentication auth = login("APPROLE_office");
        when(dataScopeService.getScope(auth)).thenReturn("OWN");
        when(ownOfficeContextService.getOfficeId(auth)).thenReturn(null);

        assertFalse(service.canAccessOffice(auth, 1001));

        assertThrows(
            AccessDeniedException.class,
            () -> service.getRestrictedOfficeId(auth)
        );
    }

    @Test
    void unauthenticatedUserIsDenied() {
        assertFalse(service.canAccessOffice(null, 1001));

        var anonymous = new AnonymousAuthenticationToken(
            "key", "anonymous",
            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
        );

        assertFalse(service.canAccessAllOffices(anonymous));
        assertFalse(service.canAccessOffice(anonymous, 1001));

        assertThrows(
            AccessDeniedException.class,
            () -> service.getRestrictedOfficeId(anonymous)
        );
    }

    @Test
    void unknownRoleCannotAccessAllOffices() {
        Authentication auth = login("APPROLE_unknown");

        assertFalse(service.canAccessAllOffices(auth));
        assertFalse(service.canAccessOffice(auth, 1001));
    }

    @Test
    void missingScopeIsDenied() {
        Authentication auth = login("APPROLE_staff");
        when(dataScopeService.getScope(auth)).thenReturn(null);

        assertFalse(service.canAccessOffice(auth, 1001));

        assertThrows(
            AccessDeniedException.class,
            () -> service.getRestrictedOfficeId(auth)
        );
    }
}