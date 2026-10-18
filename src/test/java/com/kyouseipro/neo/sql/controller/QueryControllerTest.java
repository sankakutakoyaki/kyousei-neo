package com.kyouseipro.neo.sql.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.domain.business.order.query.OrderQueryBuilderProvider;
import com.kyouseipro.neo.domain.corporation.api.company.query.CompanySqlProvider;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.security.OfficeAccessService;
import com.kyouseipro.neo.security.QueryAuthorizationService;
import com.kyouseipro.neo.sql.common.QueryParamBinder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.*;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.sql.service.SqlColumnValidator;

class QueryControllerTest {

    private BaseSqlRepository baseRepository;
    private QueryController controller;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                List.of(new SimpleGrantedAuthority("APPROLE_admin"))
            )
        );

        baseRepository = mock(BaseSqlRepository.class);
        SqlColumnValidator columnValidator = mock(SqlColumnValidator.class);
        controller = new QueryController(
            new SqlProvider(List.of(new CompanySqlProvider())),
            null, null, baseRepository, null,
            new QueryBuilderProvider(List.of()),
            new QueryHandlerProvider(List.of()),
            columnValidator,
            mock(OfficeAccessService.class),
            allowedAuthorization()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private SelectRequest request(Map<String, Object> params) {
        SelectRequest req = new SelectRequest();
        req.setQueryId("companySave");
        req.setParams(params);
        return req;
    }

    @Test
    void insertUsesInitialState() {
        when(baseRepository.insert(any(), anyMap(), anyString())).thenReturn(100L);

        controller.request(request(Map.of(
            "companyId", 0,
            "companyName", "テスト会社",
            "state", State.DELETE.getCode()
        )));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(baseRepository).insert(any(), captor.capture(), anyString());

        assertEquals(State.INITIAL.getCode(), captor.getValue().get("state"));
        assertEquals("テスト会社", captor.getValue().get("companyName"));
        assertFalse(captor.getValue().containsKey("version"));
    }

    @Test
    void updateRejectsState() {
        assertThrows(IllegalArgumentException.class, () ->
            controller.request(request(Map.of(
                "companyId", 100,
                "version", 1,
                "state", State.DELETE.getCode()
            )))
        );
        verifyNoInteractions(baseRepository);
    }

    @Test
    void updateRejectsSystemColumns() {
        for (String column : List.of("createDate", "updateDate", "createdAt", "updatedAt")) {
            assertThrows(IllegalArgumentException.class, () ->
                controller.request(request(Map.of(
                    "companyId", 100,
                    "version", 1,
                    column, "2026-10-15"
                )))
            );
        }
        verifyNoInteractions(baseRepository);
    }

    @Test
    void saveKeepsNormalColumns() {
        when(baseRepository.update(any(), anyMap(), anyString())).thenReturn(1);

        Object result = controller.request(request(Map.of(
            "companyId", 100,
            "version", 1,
            "companyName", "協成産業株式会社"
        )));

        assertEquals(Map.of("data", 100, "count", 1), result);

        verify(baseRepository).update(
            any(),
            argThat(params ->
                params.get("companyId").equals(100)
                && params.get("version").equals(1)
                && params.get("companyName").equals("協成産業株式会社")
                && !params.containsKey("state")
            ),
            eq("test@example.com")
        );
    }

    @Test
    void insertRejectsSystemColumns() {
        assertThrows(IllegalArgumentException.class, () ->
            controller.request(request(Map.of(
                "companyId", 0,
                "companyName", "テスト会社",
                "createDate", "2026-10-15"
            )))
        );
        verifyNoInteractions(baseRepository);
    }

    @Test
    void saveIgnoresClientEditor() {
        when(baseRepository.update(any(), anyMap(), anyString())).thenReturn(1);

        controller.request(request(Map.of(
            "companyId", 100,
            "version", 1,
            "companyName", "テスト会社",
            "editor", "another@example.com"
        )));

        verify(baseRepository).update(
            any(),
            anyMap(),
            eq("test@example.com")
        );
    }

    @Test
    void saveRejectsUnauthenticatedUser() {
        SecurityContextHolder.clearContext();

        assertThrows(BusinessException.class, () ->
            controller.request(request(Map.of(
                "companyId", 100,
                "version", 1,
                "companyName", "テスト会社"
            )))
        );

        verifyNoInteractions(baseRepository);
    }

    @Test
    void handlerReceivesAuthenticatedEditor() {
        assertHandlerEditor(
            Map.of("orderId", 1004),
            "test@example.com"
        );
    }

    @Test
    void handlerOverridesClientEditor() {
        assertHandlerEditor(
            Map.of(
                "orderId", 1004,
                "editor", "system"
            ),
            "test@example.com"
        );
    }

    private void assertHandlerEditor(Map<String, Object> params, String expectedEditor) {
        QueryHandler handler = mock(QueryHandler.class);
        QueryHandlerProvider handlerProvider = mock(QueryHandlerProvider.class);
        SqlProvider sqlProvider = mock(SqlProvider.class);

        QueryDefinition def = mock(QueryDefinition.class);

        when(def.getKind()).thenReturn(QueryKind.ORDER_SAVE);
        when(sqlProvider.get(any(QueryId.class))).thenReturn(def);
        when(handlerProvider.get(QueryKind.ORDER_SAVE)).thenReturn(handler);

        QueryController target = new QueryController(
            sqlProvider,
            null, null, baseRepository, null,
            new QueryBuilderProvider(List.of()),
            handlerProvider,
            mock(SqlColumnValidator.class),
            mock(OfficeAccessService.class),
            allowedAuthorization()
        );

        SelectRequest req = new SelectRequest();
        req.setQueryId("orderSave");
        req.setParams(params);

        target.request(req);

        ArgumentCaptor<SelectRequest> captor =
            ArgumentCaptor.forClass(SelectRequest.class);

        verify(handler).execute(eq(def), captor.capture());

        assertEquals(
            expectedEditor,
            captor.getValue().getParams().get("editor")
        );
    }

    @Test
    void orderListUsesServerOfficeId() {
        OfficeAccessService officeAccessService = mock(OfficeAccessService.class);
        SqlRepository sqlRepository = mock(SqlRepository.class);
        QueryParamBinder paramBinder = mock(QueryParamBinder.class);

        when(officeAccessService.getRestrictedOfficeId(any())).thenReturn(1001);

        var orderController = new QueryController(
            new SqlProvider(List.of()),
            paramBinder,
            sqlRepository,
            baseRepository,
            null,
            new QueryBuilderProvider(List.of(new OrderQueryBuilderProvider())),
            new QueryHandlerProvider(List.of()),
            mock(SqlColumnValidator.class),
            officeAccessService,
            allowedAuthorization()
        );

        SelectRequest req = new SelectRequest();
        req.setQueryId("orderList");
        req.setParams(new java.util.HashMap<>(Map.of(
            "category", 1,
            "allowedOfficeId", 1002
        )));

        orderController.request(req);

        assertEquals(1001, req.getParams().get("allowedOfficeId"));
        verify(officeAccessService).getRestrictedOfficeId(any());
    }

    @Test
    void orderListAllowsMasterAllOffices() {
        OfficeAccessService officeAccessService = mock(OfficeAccessService.class);
        when(officeAccessService.getRestrictedOfficeId(any())).thenReturn(null);

        var orderController = new QueryController(
            new SqlProvider(List.of()),
            mock(QueryParamBinder.class),
            mock(SqlRepository.class),
            baseRepository,
            null,
            new QueryBuilderProvider(List.of(new OrderQueryBuilderProvider())),
            new QueryHandlerProvider(List.of()),
            mock(SqlColumnValidator.class),
            officeAccessService,
            allowedAuthorization()
        );

        SelectRequest req = new SelectRequest();
        req.setQueryId("orderList");
        req.setParams(new java.util.HashMap<>(Map.of(
            "category", 1,
            "allowedOfficeId", 1002
        )));

        orderController.request(req);

        assertTrue(req.getParams().containsKey("allowedOfficeId"));
        assertNull(req.getParams().get("allowedOfficeId"));
        verify(officeAccessService).getRestrictedOfficeId(any());
    }

    @Test
    void orderListRejectsUnknownOffice() {
        OfficeAccessService officeAccessService = mock(OfficeAccessService.class);

        when(officeAccessService.getRestrictedOfficeId(any()))
            .thenThrow(new org.springframework.security.access.AccessDeniedException(
                "所属営業所を確認できません。"
            ));

        var orderController = new QueryController(
            new SqlProvider(List.of()),
            mock(QueryParamBinder.class),
            mock(SqlRepository.class),
            baseRepository,
            null,
            new QueryBuilderProvider(List.of(new OrderQueryBuilderProvider())),
            new QueryHandlerProvider(List.of()),
            mock(SqlColumnValidator.class),
            officeAccessService,
            allowedAuthorization()
        );

        SelectRequest req = new SelectRequest();
        req.setQueryId("orderList");
        req.setParams(Map.of("category", 1));

        assertThrows(
            org.springframework.security.access.AccessDeniedException.class,
            () -> orderController.request(req)
        );
    }

    @Test
    void orderListBindsServerOfficeId() {
        OfficeAccessService officeAccessService = mock(OfficeAccessService.class);
        SqlRepository sqlRepository = mock(SqlRepository.class);

        when(officeAccessService.getRestrictedOfficeId(any())).thenReturn(1001);

        var orderController = new QueryController(
            new SqlProvider(List.of()),
            new QueryParamBinder(),
            sqlRepository,
            baseRepository,
            null,
            new QueryBuilderProvider(List.of(new OrderQueryBuilderProvider())),
            new QueryHandlerProvider(List.of()),
            mock(SqlColumnValidator.class),
            officeAccessService,
            allowedAuthorization()
        );

        SelectRequest req = new SelectRequest();
        req.setQueryId("orderList");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("category", 1);
        params.put("state", State.INITIAL.getCode());
        params.put("compState", State.DELETE.getCode());
        params.put("dateFrom", "2026-10-01");
        params.put("dateTo", "2026-11-01");
        params.put("ownOfficeId", null);
        params.put("allowedOfficeId", 1002);

        req.setParams(params);

        orderController.request(req);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Object>> captor = ArgumentCaptor.forClass(List.class);

        verify(sqlRepository).selectMap(anyString(), captor.capture());

        List<Object> boundParams = captor.getValue();
        assertEquals(1001, boundParams.get(boundParams.size() - 2));
        assertEquals(1001, boundParams.get(boundParams.size() - 1));
    }

    private QueryAuthorizationService allowedAuthorization() {
        QueryAuthorizationService service =
            mock(QueryAuthorizationService.class);

        when(service.isAllowed(any(), any(), any()))
            .thenReturn(true);

        return service;
    }

    @Test
    void deniedPermissionDoesNotExecuteHandler() {
        QueryHandler handler = mock(QueryHandler.class);
        QueryHandlerProvider handlerProvider = mock(QueryHandlerProvider.class);
        SqlProvider sqlProvider = mock(SqlProvider.class);

        QueryDefinition def = mock(QueryDefinition.class);
        when(def.getKind()).thenReturn(QueryKind.ORDER_SAVE);
        when(sqlProvider.get(any(QueryId.class))).thenReturn(def);

        QueryAuthorizationService authorizationService =
            mock(QueryAuthorizationService.class);

        QueryController target = new QueryController(
            sqlProvider,
            null, null, baseRepository, null,
            new QueryBuilderProvider(List.of()),
            handlerProvider,
            mock(SqlColumnValidator.class),
            mock(OfficeAccessService.class),
            authorizationService
        );

        SelectRequest req = new SelectRequest();
        req.setQueryId("orderSave");
        req.setParams(Map.of("orderId", 1004));

        assertThrows(
            org.springframework.security.access.AccessDeniedException.class,
            () -> target.request(req)
        );

        verify(authorizationService).isAllowed(any(), eq(def), eq(req));
        verifyNoInteractions(handler, baseRepository);
        verify(handlerProvider, never()).get(any());
    }
}