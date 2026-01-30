package com.kyouseipro.neo.personnel.workingconditions.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.dto.ApiResponse;
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
    public ResponseEntity<WorkingConditionsEntity> getById(@RequestBody IdRequest req) {
            return ResponseEntity.ok(workingConditionsService.getById(req.getId()).orElse(null));
    }

    /**
     * EmployeeIDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/employeeid")
	@ResponseBody
    public ResponseEntity<WorkingConditionsEntity> getByEmployeeId(@RequestBody IdRequest req) {
            return ResponseEntity.ok(workingConditionsService.getByEmployeeId(req.getId()).orElse(null));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody WorkingConditionsEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workingConditionsService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int id = workingConditionsService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return workingConditionsService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}