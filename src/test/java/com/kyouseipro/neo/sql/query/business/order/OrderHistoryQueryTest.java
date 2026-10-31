package com.kyouseipro.neo.sql.query.business.order;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.domain.business.order.query.OrderSqlProvider;

class OrderHistoryQueryTest {
    @Test void historyPairsExactVersionsAfterApplyingExistingAccessAndPagination() {
        var definition = OrderQuery.orderHistoryList();
        String sql = definition.getSql();
        assertTrue(sql.contains("WITH history_page AS"));
        assertTrue(sql.contains("version = h.order_version - 1"));
        assertTrue(sql.contains("version = h.order_version\n"));
        assertTrue(sql.contains("COUNT(*) OVER () AS snapshot_count"));
        assertTrue(sql.indexOf("dcm.employee_id = ?") < sql.indexOf("FROM history_page h"));
        assertEquals(List.of("state", "orderId", "beforeHistoryId", "beforeHistoryId",
            "accessScope", "accessScope", "accessOfficeId", "accessScope", "accessEmployeeId"), definition.getParamOrder());
        assertEquals(definition.getParamOrder().size(), sql.chars().filter(c -> c == '?').count());
        assertEquals("orders", new OrderSqlProvider().provide().get(QueryId.ORDER_HISTORY_LIST).getPermissionTable());
    }

    @Test void dispatchLogsAreMatchedByOrderAssignmentAndExactVersion() {
        String sql = OrderQuery.orderHistoryList().getSql();
        assertTrue(sql.contains("h.dispatch_assignment_id, h.dispatch_version"));
        assertTrue(sql.contains("dispatch_assignment_id = h.dispatch_assignment_id AND order_id = h.order_id"));
        assertTrue(sql.contains("version = h.dispatch_version - 1"));
        assertTrue(sql.contains("version = h.dispatch_version\n"));
        assertTrue(sql.contains("h.dispatch_assignment_id IS NULL"));
        assertTrue(sql.contains("dispatch_after.snapshot_count AS after_dispatch_count"));
    }
}
