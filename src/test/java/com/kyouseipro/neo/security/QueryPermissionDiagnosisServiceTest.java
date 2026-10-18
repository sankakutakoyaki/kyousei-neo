package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.SqlProvider;
import com.kyouseipro.neo.sql.provider.Tables;

class QueryPermissionDiagnosisServiceTest {

    @Test
    void reportsMissingPermissionDefinitions() {
        SqlProvider provider = mock(SqlProvider.class);
        QueryPermissionValidator validator = new QueryPermissionValidator();

        QueryDefinition select = QueryDefinition.select(
            "SELECT * FROM orders",
            java.util.List.of()
        );

        QueryDefinition save = new QueryDefinition(
            QueryType.UPDATE,
            QueryKind.SAVE,
            Tables.ORDER_BY_IDS
        );

        when(provider.getDefinitions()).thenReturn(Map.of(
            QueryId.ORDER_DETAIL, select,
            QueryId.ORDER_SAVE, save
        ));

        QueryPermissionDiagnosisService service =
            new QueryPermissionDiagnosisService(provider, validator);

        var issues = service.diagnose();

        assertEquals(1, issues.size());
        assertEquals("orderDetail", issues.get(0).queryId());
        assertTrue(
            issues.get(0).message().contains("permissionTable")
        );
    }

    @Test
    void validDefinitionsProduceNoIssues() {
        SqlProvider provider = mock(SqlProvider.class);
        QueryPermissionValidator validator = new QueryPermissionValidator();

        QueryDefinition select = QueryDefinition.select(
            "SELECT * FROM orders",
            java.util.List.of()
        );
        select.setPermissionTable("orders");

        when(provider.getDefinitions()).thenReturn(Map.of(
            QueryId.ORDER_DETAIL, select
        ));

        QueryPermissionDiagnosisService service =
            new QueryPermissionDiagnosisService(provider, validator);

        assertTrue(service.diagnose().isEmpty());
    }

    @Test
    void diagnoseActualSqlProviders() {
        var providers = java.util.List.of(
            new com.kyouseipro.neo.domain.business.order.query.OrderSqlProvider(),
            new com.kyouseipro.neo.domain.operations.dispatch.query.DailyCrewSqlProvider(),
            new com.kyouseipro.neo.domain.operations.dispatch.query.DailyCrewMemberSqlProvider(),
            new com.kyouseipro.neo.domain.operations.dispatch.query.DispatchAssignmentSqlProvider()
        );

        SqlProvider sqlProvider = new SqlProvider(providers);
        QueryPermissionDiagnosisService service =
            new QueryPermissionDiagnosisService(
                sqlProvider,
                new QueryPermissionValidator()
            );

        var issues = service.diagnose();

        System.out.println("\n=== Query Permission Diagnosis ===");

        for (var issue : issues) {
            System.out.println(
                issue.queryId() + " : " + issue.message()
            );
        }

        System.out.println("未設定件数: " + issues.size());
    }
}