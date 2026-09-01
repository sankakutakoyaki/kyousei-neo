package com.kyouseipro.neo.domain.management.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.domain.management.application.TimeworkService;
import com.kyouseipro.neo.domain.management.model.StampRequest;
import com.kyouseipro.neo.domain.management.model.TimeworkListItem;
import com.kyouseipro.neo.domain.management.model.TimeworkStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/timeworks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
public class TimeworkController {
    private final TimeworkService service;

    @GetMapping("/today")
    public SimpleResponse<TimeworkStatus> today(Authentication authentication) {
        return SimpleResponse.ok(service.findToday(authentication));
    }

    @GetMapping("/list")
    public SimpleResponse<List<TimeworkListItem>> list(
        Authentication authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
        @RequestParam(required = false) Long officeId
    ) {
        return SimpleResponse.ok(service.findList(authentication, workDate, officeId));
    }

    @PostMapping("/stamp")
    public SimpleResponse<TimeworkStatus> stamp(Authentication authentication, @RequestBody StampRequest request) {
        TimeworkStatus status = service.stamp(authentication, request.stampType());
        String message = "START".equalsIgnoreCase(request.stampType())
            ? "出勤を打刻しました。" : "退勤を打刻しました。";
        return SimpleResponse.ok(message, status);
    }
}
