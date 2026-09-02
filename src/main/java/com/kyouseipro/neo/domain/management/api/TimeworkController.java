package com.kyouseipro.neo.domain.management.api;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.domain.management.application.TimeworkService;
import com.kyouseipro.neo.domain.management.model.StampRequest;
import com.kyouseipro.neo.domain.management.model.TimeworkListItem;
import com.kyouseipro.neo.domain.management.model.TimeworkStatus;
import com.kyouseipro.neo.domain.management.model.TimeworkPeriod;
import com.kyouseipro.neo.domain.management.model.TimeworkUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/timeworks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
public class TimeworkController {
    private static final DateTimeFormatter CSV_DATE_TIME = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private final TimeworkService service;

    @GetMapping("/employee")
    public SimpleResponse<TimeworkStatus> employee(@RequestParam String identifier) {
        return SimpleResponse.ok(service.findEmployee(identifier));
    }

    @GetMapping("/list")
    public SimpleResponse<List<TimeworkListItem>> list() {
        return SimpleResponse.ok(service.findTodayList());
    }

    @PostMapping("/stamp")
    public SimpleResponse<TimeworkStatus> stamp(Authentication authentication, @RequestBody StampRequest request) {
        TimeworkStatus status = service.stamp(authentication, request.employeeId(), request.stampType());
        String message = "START".equalsIgnoreCase(request.stampType())
            ? "出勤を打刻しました。" : "退勤を打刻しました。";
        return SimpleResponse.ok(message, status);
    }

    @GetMapping("/admin/list")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff')")
    public SimpleResponse<List<TimeworkListItem>> managementList(
        @RequestParam String targetMonth,
        @RequestParam String closingType,
        @RequestParam(required = false) Long officeId
    ) {
        return SimpleResponse.ok(service.findManagementList(targetMonth, closingType, officeId));
    }

    @PostMapping("/admin/update")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff')")
    public SimpleResponse<Void> update(
        Authentication authentication,
        @RequestBody TimeworkUpdateRequest request
    ) {
        service.updateTimes(authentication, request);
        return SimpleResponse.ok("保存しました。", null);
    }

    @GetMapping(value = "/admin/csv", produces = "text/csv")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff')")
    public ResponseEntity<byte[]> csv(
        @RequestParam String targetMonth,
        @RequestParam String closingType,
        @RequestParam Long officeId
    ) {
        TimeworkPeriod period = service.period(targetMonth, closingType);
        List<TimeworkListItem> items = service.findManagementList(targetMonth, closingType, officeId);
        StringBuilder csv = new StringBuilder("\uFEFF勤務日,社員ID,氏名,営業所,出勤,退勤\r\n");
        for (TimeworkListItem item : items) {
            csv.append(item.workDate()).append(',')
                .append(item.employeeId()).append(',')
                .append(csvValue(item.fullName())).append(',')
                .append(csvValue(item.officeName())).append(',')
                .append(item.startTime() == null ? "" : CSV_DATE_TIME.format(item.startTime())).append(',')
                .append(item.endTime() == null ? "" : CSV_DATE_TIME.format(item.endTime())).append("\r\n");
        }
        String fileName = "勤怠_" + period.from() + "_" + period.to() + ".csv";
        return ResponseEntity.ok()
            .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build().toString())
            .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String csvValue(String value) {
        String normalized = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + normalized + "\"";
    }
}
