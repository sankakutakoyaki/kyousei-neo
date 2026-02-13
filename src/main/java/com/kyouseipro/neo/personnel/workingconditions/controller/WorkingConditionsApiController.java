package com.kyouseipro.neo.personnel.workingconditions.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.personnel.workingconditions.entity.WorkingConditionsEntity;
import com.kyouseipro.neo.personnel.workingconditions.service.WorkingConditionsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/working_conditions")
public class WorkingConditionsApiController {
    private final WorkingConditionsService workingConditionsService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/id")
	@ResponseBody
    public ResponseEntity<SimpleResponse<WorkingConditionsEntity>> getById(@RequestBody IdRequest req) {
            return ResponseEntity.ok(SimpleResponse.ok(workingConditionsService.getById(req.getId())));
    }

    /**
     * EmployeeIDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/employeeid")
	@ResponseBody
    public ResponseEntity<SimpleResponse<WorkingConditionsEntity>> getByEmployeeId(@RequestBody IdRequest req) {
            return ResponseEntity.ok(SimpleResponse.ok(workingConditionsService.getByEmployeeId(req.getId())));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/save")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> save(@RequestBody WorkingConditionsEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workingConditionsService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/delete")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int id = workingConditionsService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    // @PostMapping("/download/csv")
	// @ResponseBody
    // public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
    //     return workingConditionsService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    // }
    @PostMapping(value = "/download/csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        String csv = workingConditionsService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv";

        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }
}