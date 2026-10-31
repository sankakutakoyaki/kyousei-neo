package com.kyouseipro.neo.domain.operations.dispatch.api;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kyouseipro.neo.domain.operations.dispatch.result.WorkResultService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-orders")
@PreAuthorize("hasAuthority('APPROLE_user')")
public class MyOrderController {

    private final SqlRepository repository;
    private final OwnOfficeContextService ownOfficeContextService;
    private final WorkResultService workResults;

    @GetMapping
    public List<Map<String, Object>> find(
            Authentication authentication,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        Long employeeId = ownOfficeContextService.getEmployeeId(authentication);

        if (employeeId == null || employeeId <= 0) {
            throw new AccessDeniedException("従業員情報を確認できません。");
        }

        return repository.selectMap("""
            SELECT
                dc.daily_crew_id,
                dc.work_date,
                dc.office_id AS crew_office_id,
                dc.vehicle_id,
                dc.dispatch_category,
                da.dispatch_assignment_id,
                da.visit_order,
                wr.result_type,
                o.order_id,
                o.own_office_id,
                o.request_number,
                o.visit_date,
                o.visit_time,
                o.title,
                o.full_address,
                o.remarks
            FROM daily_crew_members dcm
            JOIN daily_crews dc
                ON dc.daily_crew_id = dcm.daily_crew_id
               AND dc.state = ?
            JOIN dispatch_assignments da
                ON da.daily_crew_id = dc.daily_crew_id
               AND da.state = ?
            JOIN orders o
                ON o.order_id = da.order_id
            LEFT JOIN dbo.order_work_results wr ON wr.dispatch_assignment_id=da.dispatch_assignment_id AND wr.work_date=dc.work_date AND wr.state=0
            WHERE dcm.employee_id = ?
              AND dcm.state = ?
              AND dc.work_date = ?
              AND o.state <> ?
            ORDER BY
                dc.display_order,
                dc.daily_crew_id,
                da.visit_order,
                da.dispatch_assignment_id
            """,
            List.of(
                State.INITIAL.getCode(),
                State.INITIAL.getCode(),
                employeeId,
                State.INITIAL.getCode(),
                Date.valueOf(date),
                State.DELETE.getCode()
            )
        );
    }

    @GetMapping("/{orderId}")
    public Map<String, Object> detail(
            Authentication authentication,
            @PathVariable Long orderId) {

        Long employeeId = ownOfficeContextService.getEmployeeId(authentication);

        if (employeeId == null || employeeId <= 0) {
            throw new AccessDeniedException("従業員情報を確認できません。");
        }

        List<Map<String, Object>> rows = repository.selectMap("""
            SELECT
                o.order_id,
                o.request_number,
                o.visit_date,
                o.visit_time,
                o.title,
                o.postal_code,
                o.full_address,
                o.contact_information,
                o.remarks,
                o.prime_constractor_id,
                o.prime_constractor_office_id,
                o.own_office_id
            FROM orders o
            WHERE o.order_id = ?
            AND o.state <> ?
            AND EXISTS (
                SELECT 1
                FROM dispatch_assignments da
                JOIN daily_crews dc
                    ON dc.daily_crew_id = da.daily_crew_id
                    AND dc.state = ?
                JOIN daily_crew_members dcm
                    ON dcm.daily_crew_id = dc.daily_crew_id
                    AND dcm.state = ?
                WHERE da.order_id = o.order_id
                    AND da.state = ?
                    AND dcm.employee_id = ?
            )
            """,
            List.of(
                orderId,
                State.DELETE.getCode(),
                State.INITIAL.getCode(),
                State.INITIAL.getCode(),
                State.INITIAL.getCode(),
                employeeId
            )
        );

        if (rows.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "担当伝票が見つかりません。"
            );
        }

        return rows.get(0);
    }

    @GetMapping("/{orderId}/lines")
    public Map<String, Object> lines(
            Authentication authentication,
            @PathVariable Long orderId) {

        Long employeeId = ownOfficeContextService.getEmployeeId(authentication);
        if (employeeId == null || employeeId <= 0) {
            throw new AccessDeniedException("従業員情報を確認できません。");
        }

        String accessSql = """
            AND EXISTS (
                SELECT 1
                FROM orders o
                JOIN dispatch_assignments da
                    ON da.order_id = o.order_id
                AND da.state = ?
                JOIN daily_crews dc
                    ON dc.daily_crew_id = da.daily_crew_id
                AND dc.state = ?
                JOIN daily_crew_members dcm
                    ON dcm.daily_crew_id = dc.daily_crew_id
                AND dcm.state = ?
                WHERE o.order_id = ?
                AND o.state <> ?
                AND dcm.employee_id = ?
            )
            """;

        List<Object> accessParams = List.of(
            State.INITIAL.getCode(),
            State.INITIAL.getCode(),
            State.INITIAL.getCode(),
            orderId,
            State.DELETE.getCode(),
            employeeId
        );

        List<Object> params = new java.util.ArrayList<>();
        params.add(State.INITIAL.getCode());
        params.add(orderId);
        params.addAll(accessParams);

        List<Map<String, Object>> items = repository.selectMap("""
            SELECT
                oi.order_item_id,
                oi.order_id,
                oi.jan_code,
                oi.item_maker,
                oi.item_name,
                oi.item_model,
                oi.item_quantity,
                oi.remarks
            FROM order_items oi
            WHERE oi.state = ?
            AND oi.order_id = ?
            """ + accessSql + """
            ORDER BY oi.order_item_id
            """, params);

        List<Map<String, Object>> works = repository.selectMap("""
            SELECT
                ow.order_work_id,
                ow.order_id,
                ow.order_work_code,
                ow.order_work_name,
                ow.order_work_quantity,
                ow.remarks
            FROM order_works ow
            WHERE ow.state = ?
            AND ow.order_id = ?
            """ + accessSql + """
            ORDER BY ow.order_work_id
            """, params);

        return Map.of(
            "items", items,
            "works", works
        );
    }

    @GetMapping("/{orderId}/assignments/{assignmentId}/result")
    public Map<String,Object> result(Authentication auth, @PathVariable long orderId, @PathVariable long assignmentId) {
        return workResults.detail(auth, orderId, assignmentId);
    }

    @PostMapping("/{orderId}/assignments/{assignmentId}/result")
    public Map<String,Object> report(Authentication auth, @PathVariable long orderId, @PathVariable long assignmentId,
            @RequestBody WorkResultService.Request req) {
        return workResults.save(auth, orderId, assignmentId, req);
    }
}