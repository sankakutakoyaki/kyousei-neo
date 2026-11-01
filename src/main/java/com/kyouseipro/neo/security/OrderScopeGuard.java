package com.kyouseipro.neo.security;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderScopeGuard {
    private final OrderAccessService access;
    private final PermissionService permissions;
    private final SqlRepository sql;
    private final com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService context;
    public Map<String,Object> require(Authentication auth, long id, boolean officeWrite) {
        if(officeWrite && (!permissions.canTable(auth,"orders","update") || !permissions.canOperation(auth,"order.save")))
            throw new AccessDeniedException("受注の操作権限がありません。");
        var scope=officeWrite?access.getAccessCondition(auth):readCondition(auth);
        var rows=sql.selectMap("""
            SELECT o.* FROM dbo.orders o WHERE o.order_id=? AND o.state=0 AND (
                ?='ALL' OR (? IN ('OWN','OWN_ASSIGNED') AND o.own_office_id=?)
                OR (? IN ('ASSIGNED','OWN_ASSIGNED') AND EXISTS (
                    SELECT 1 FROM dbo.dispatch_assignments da
                    JOIN dbo.daily_crews dc ON dc.daily_crew_id=da.daily_crew_id AND dc.state=0
                    JOIN dbo.daily_crew_members dcm ON dcm.daily_crew_id=dc.daily_crew_id AND dcm.state=0
                    WHERE da.order_id=o.order_id AND da.state=0 AND dcm.employee_id=?)))
            """,Arrays.asList(id,scope.scope(),scope.scope(),scope.officeId(),scope.scope(),scope.employeeId()));
        if(rows.isEmpty())throw new AccessDeniedException("この受注を操作できません。");
        return rows.get(0);
    }
    public OrderAccessService.AccessCondition readCondition(Authentication auth) {
        if(permissions.canTable(auth,"orders","select"))return access.getAccessCondition(auth);
        // 事務用テーブル権限がなくても、役割名に依存せず本人の担当だけを許可する。
        if(auth!=null && auth.isAuthenticated() && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            Long employee=context.getEmployeeId(auth);
            if(employee!=null && employee>0)return new OrderAccessService.AccessCondition("ASSIGNED",null,employee);
        }
        throw new AccessDeniedException("受注の参照権限がありません。");
    }

}
