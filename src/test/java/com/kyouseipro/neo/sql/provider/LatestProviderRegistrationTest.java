package com.kyouseipro.neo.sql.provider;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.interfaces.sql.QueryBuilderPart;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;

class LatestProviderRegistrationTest {
    @SuppressWarnings("unchecked")
    private static <T> T instance(String name) throws Exception {
        var type = Class.forName(name);
        var constructor = type.getConstructors()[0];
        Object[] params = new Object[constructor.getParameterCount()];
        for(int i=0; i<params.length; i++) {
            var param = constructor.getParameterTypes()[i];
            if(param.getSimpleName().endsWith("QueryBuilder")) params[i] = instance(param.getName());
        }
        return (T) constructor.newInstance(params);
    }
    @Test void latestSqlPartsHaveUniqueRegistrationsIncludingDispatchAndWorkMaster() throws Exception {
        var provider = new SqlProvider(List.<SqlProviderPart>of(
            instance("com.kyouseipro.neo.domain.business.order.query.OrderSqlProvider"),
            instance("com.kyouseipro.neo.domain.business.recycle.query.RecycleSqlProvider"),
            instance("com.kyouseipro.neo.domain.corporation.api.company.query.CompanySqlProvider"),
            instance("com.kyouseipro.neo.domain.corporation.api.office.query.OfficeSqlProvider"),
            instance("com.kyouseipro.neo.domain.corporation.api.staff.query.StaffSqlProvider"),
            instance("com.kyouseipro.neo.domain.master.item.query.ItemMasterSqlProvider"),
            instance("com.kyouseipro.neo.domain.master.work.query.WorkMasterSqlProvider"),
            instance("com.kyouseipro.neo.domain.operations.dispatch.query.DailyCrewMemberSqlProvider"),
            instance("com.kyouseipro.neo.domain.operations.dispatch.query.DailyCrewSqlProvider"),
            instance("com.kyouseipro.neo.domain.operations.dispatch.query.DispatchAssignmentSqlProvider"),
            instance("com.kyouseipro.neo.domain.operations.dispatch.query.DispatchOrderSqlProvider"),
            instance("com.kyouseipro.neo.domain.operations.shift.query.EmployeeShiftSqlProvider"),
            instance("com.kyouseipro.neo.domain.operations.vehicle.query.VehicleDefaultMemberSqlProvider"),
            instance("com.kyouseipro.neo.domain.operations.vehicle.query.VehicleDispatchCategorySqlProvider"),
            instance("com.kyouseipro.neo.domain.operations.vehicle.query.VehicleSqlProvider"),
            instance("com.kyouseipro.neo.domain.personnel.employee.query.EmployeeSqlProvider")));
        assertNotNull(provider.get(QueryId.DISPATCH_ASSIGNMENT_SAVE));
        assertNotNull(provider.get(QueryId.DAILY_CREW_MEMBER_SAVE));
        assertNotNull(provider.get(QueryId.WORK_MASTER_LIST));
    }
    @Test void latestBuilderPartsHaveUniqueRegistrations() throws Exception {
        var provider = new QueryBuilderProvider(List.<QueryBuilderPart>of(
            instance("com.kyouseipro.neo.domain.business.order.query.OrderQueryBuilderProvider"),
            instance("com.kyouseipro.neo.domain.business.recycle.query.RecycleQueryBuilderProvider"),

            instance("com.kyouseipro.neo.sql.query.business.order.OrderItemQueryBuilderPart")));
        assertNotNull(provider.get(QueryId.ORDER_ITEM_LIST));
    }
    @Test void latestHandlerKindsHaveUniqueOwners() throws Exception {
        var provider = new QueryHandlerProvider(List.<QueryHandler>of(
            instance("com.kyouseipro.neo.sql.handler.DailyCrewHandler"),
            instance("com.kyouseipro.neo.sql.handler.DispathAssignmentHandler"),
            instance("com.kyouseipro.neo.sql.handler.EmployeeHandler"),
            instance("com.kyouseipro.neo.sql.handler.EmployeeShiftHandler"),
            instance("com.kyouseipro.neo.sql.handler.OrderHandler"),
            instance("com.kyouseipro.neo.sql.handler.OrderItemArrivalHandler"),
            instance("com.kyouseipro.neo.sql.handler.RecycleHandler"),
            instance("com.kyouseipro.neo.sql.handler.VehicleDefaultMemberHandler")));
        assertNotNull(provider.get(QueryKind.DISPATCH_ASSIGNMENT_REORDER));
        assertNotNull(provider.get(QueryKind.DAILY_CREW_MEMBER_SAVE));
    }
    private static class FirstSql implements SqlProviderPart {
        public Map<QueryId,QueryDefinition> provide() {
            return Map.of(QueryId.ORDER_LIST, QueryDefinition.select("SELECT 1",List.of()));
        }
    }
    private static class SecondSql extends FirstSql {}
    @Test void duplicateSqlRegistrationReportsBothSources() {
        var error = assertThrows(IllegalStateException.class,
            () -> new SqlProvider(List.of(new FirstSql(),new SecondSql())));
        assertTrue(error.getMessage().contains(FirstSql.class.getName()));
        assertTrue(error.getMessage().contains(SecondSql.class.getName()));
    }
    private static class FirstHandler implements QueryHandler {
        public boolean supports(QueryKind kind) {return kind==QueryKind.ORDER_SAVE;}
        public Object execute(QueryDefinition definition,SelectRequest request) {return null;}
    }
    private static class SecondHandler extends FirstHandler {}
    @Test void ambiguousHandlerFailsAtRegistration() {
        assertThrows(IllegalStateException.class,
            () -> new QueryHandlerProvider(List.of(new FirstHandler(),new SecondHandler())));
    }
}
