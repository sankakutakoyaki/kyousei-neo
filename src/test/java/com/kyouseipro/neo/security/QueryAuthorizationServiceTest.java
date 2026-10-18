package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;

class QueryAuthorizationServiceTest {

    private PermissionService permissionService;
    private QueryAuthorizationService service;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        permissionService = mock(PermissionService.class);
        service = new QueryAuthorizationService(permissionService);
        authentication = mock(Authentication.class);
    }

    private QueryDefinition definition(QueryKind kind) {
        return new QueryDefinition(
            QueryType.UPDATE,
            kind,
            Tables.ITEM_MASTER_BY_IDS
        );
    }

    private SelectRequest request(Object id) {
        SelectRequest req = new SelectRequest();

        if (id == null) {
            req.setParams(Map.of());
        } else {
            req.setParams(Map.of("itemMasterId", id));
        }

        return req;
    }

    @Test
    void insertRequiresInsertPermission() {
        when(permissionService.canTable(
            authentication, "item_masters", "insert"
        )).thenReturn(true);

        assertTrue(service.isAllowed(
            authentication,
            definition(QueryKind.INSERT),
            request(null)
        ));
    }

    @Test
    void updateRequiresUpdatePermission() {
        when(permissionService.canTable(
            authentication, "item_masters", "update"
        )).thenReturn(true);

        assertTrue(service.isAllowed(
            authentication,
            definition(QueryKind.UPDATE),
            request(25)
        ));
    }

    @Test
    void newSaveRequiresInsertPermission() {
        when(permissionService.canTable(
            authentication, "item_masters", "insert"
        )).thenReturn(true);

        assertTrue(service.isAllowed(
            authentication,
            definition(QueryKind.SAVE),
            request(null)
        ));

        verify(permissionService).canTable(
            authentication, "item_masters", "insert"
        );
    }

    @Test
    void existingSaveRequiresUpdatePermission() {
        when(permissionService.canTable(
            authentication, "item_masters", "update"
        )).thenReturn(true);

        assertTrue(service.isAllowed(
            authentication,
            definition(QueryKind.SAVE),
            request(25)
        ));

        verify(permissionService).canTable(
            authentication, "item_masters", "update"
        );
    }

    @Test
    void deleteRequiresDeletePermission() {
        when(permissionService.canTable(
            authentication, "item_masters", "delete"
        )).thenReturn(true);

        assertTrue(service.isAllowed(
            authentication,
            definition(QueryKind.DELETE_BY_IDS),
            request(25)
        ));
    }

    @Test
    void deniedPermissionReturnsFalse() {
        assertFalse(service.isAllowed(
            authentication,
            definition(QueryKind.SAVE),
            request(25)
        ));
    }

    @Test
    void invalidSaveIdIsDenied() {
        assertFalse(service.isAllowed(
            authentication,
            definition(QueryKind.SAVE),
            request("invalid")
        ));

        verifyNoInteractions(permissionService);
    }

    @Test
    void unsupportedKindIsDenied() {
        assertFalse(service.isAllowed(
            authentication,
            definition(QueryKind.DISPATCH_ASSIGNMENT_MOVE),
            request(25)
        ));

        verifyNoInteractions(permissionService);
    }

    @Test
    void permittedOperationIsAllowed() {
        QueryDefinition def = definition(QueryKind.DISPATCH_ASSIGNMENT_MOVE);
        def.setPermissionOperation("dispatch.assignment.move");

        when(permissionService.canOperation(
            authentication, "dispatch.assignment.move"
        )).thenReturn(true);

        assertTrue(service.isAllowed(authentication, def, request(25)));

        verify(permissionService).canOperation(
            authentication, "dispatch.assignment.move"
        );
    }

    @Test
    void deniedOperationIsRejected() {
        QueryDefinition def = definition(QueryKind.DISPATCH_ASSIGNMENT_MOVE);
        def.setPermissionOperation("dispatch.assignment.move");

        assertFalse(service.isAllowed(authentication, def, request(25)));
    }

    @Test
    void missingOperationPermissionIsRejected() {
        assertFalse(service.isAllowed(
            authentication,
            definition(QueryKind.DISPATCH_ASSIGNMENT_MOVE),
            request(25)
        ));

        verifyNoInteractions(permissionService);
    }

    @Test
    void blankOperationIsRejected() {
        QueryDefinition def = definition(QueryKind.DISPATCH_ASSIGNMENT_MOVE);
        def.setPermissionOperation(" ");

        assertFalse(service.isAllowed(authentication, def, request(25)));

        verifyNoInteractions(permissionService);
    }

    @Test
    void selectRequiresTablePermission() {
        QueryDefinition def = QueryDefinition.select(
            "SELECT * FROM item_masters",
            List.of()
        );
        def.setPermissionTable("item_masters");

        when(permissionService.canTable(
            authentication, "item_masters", "select"
        )).thenReturn(true);

        assertTrue(service.isAllowed(authentication, def, request(null)));
    }

    @Test
    void csvRequiresTablePermission() {
        QueryDefinition def = QueryDefinition.csv(
            "SELECT * FROM item_masters",
            List.of(),
            List.of()
        );
        def.setPermissionTable("item_masters");

        when(permissionService.canTable(
            authentication, "item_masters", "select"
        )).thenReturn(true);

        assertTrue(service.isAllowed(authentication, def, request(null)));
    }

    @Test
    void selectWithoutPermissionTableIsDenied() {
        QueryDefinition def = QueryDefinition.select(
            "SELECT * FROM item_masters",
            List.of()
        );

        assertFalse(service.isAllowed(authentication, def, request(null)));

        verifyNoInteractions(permissionService);
    }

    @Test
    void sqlUpdateWithoutExplicitPermissionIsDenied() {
        QueryDefinition def = QueryDefinition.update(
            "UPDATE item_masters SET item_name = ?",
            List.of("itemName")
        );
        def.setPermissionTable("item_masters");

        assertFalse(service.isAllowed(authentication, def, request(null)));

        verifyNoInteractions(permissionService);
    }

    @Test
    void recyclePriceListRequiresSelectPermission() {
        QueryDefinition def = QueryDefinition.select(
            "SELECT * FROM recycle_prices", List.of()
        );
        def.setKind(QueryKind.RECYCLE_PRICE_LIST);
        def.setPermissionTable("recycle_prices");

        when(permissionService.canTable(
            authentication, "recycle_prices", "select"
        )).thenReturn(true);

        assertTrue(service.isAllowed(authentication, def, request(null)));

        verify(permissionService).canTable(
            authentication, "recycle_prices", "select"
        );
    }

    @Test
    void recyclePriceDetailRequiresSelectPermission() {
        QueryDefinition def = QueryDefinition.select(
            "SELECT * FROM recycle_prices", List.of()
        );
        def.setKind(QueryKind.RECYCLE_PRICE_DETAIL);
        def.setPermissionTable("recycle_prices");

        when(permissionService.canTable(
            authentication, "recycle_prices", "select"
        )).thenReturn(true);

        assertTrue(service.isAllowed(authentication, def, request(null)));
    }

    @Test
    void recyclePriceListWithoutSelectPermissionIsDenied() {
        QueryDefinition def = QueryDefinition.select(
            "SELECT * FROM recycle_prices", List.of()
        );
        def.setKind(QueryKind.RECYCLE_PRICE_LIST);
        def.setPermissionTable("recycle_prices");

        assertFalse(service.isAllowed(authentication, def, request(null)));

        verify(permissionService).canTable(
            authentication, "recycle_prices", "select"
        );
    }

    @Test
    void recyclePriceListWithUpdateTypeIsDenied() {
        QueryDefinition def = QueryDefinition.update(
            "UPDATE recycle_prices SET price = ?", List.of("price")
        );
        def.setKind(QueryKind.RECYCLE_PRICE_LIST);
        def.setPermissionTable("recycle_prices");

        when(permissionService.canTable(
            authentication, "recycle_prices", "select"
        )).thenReturn(true);

        assertFalse(service.isAllowed(authentication, def, request(null)));

        verifyNoInteractions(permissionService);
    }
}