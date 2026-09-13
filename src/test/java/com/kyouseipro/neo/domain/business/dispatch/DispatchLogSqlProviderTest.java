package com.kyouseipro.neo.domain.business.dispatch;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class DispatchLogSqlProviderTest {
    final DispatchLogSqlProvider provider = new DispatchLogSqlProvider();
    @Test void capturesCompletePostUpdateSnapshotWithSharedAuditColumns() {
        String output = provider.buildOutput();
        for (String column : List.of("assignment_id","order_id","employee_id","role","employee_name",
                "assigned_at","assigned_by","cancelled_at","cancelled_by"))
            assertTrue(output.contains("INSERTED." + column));
        String insert = provider.buildInsertLog("@Rows", "UPDATE");
        assertTrue(insert.contains("order_dispatch_assignments_log"));
        assertTrue(insert.contains("editor, process, log_date, dispatch_version"));
        assertTrue(insert.contains("CURRENT_TIMESTAMP"));
        assertEquals(List.of("signed-in", "UPDATE", 4), provider.buildLogParams(Map.of("editor","signed-in","dispatchVersion",4),"UPDATE"));
    }
}
