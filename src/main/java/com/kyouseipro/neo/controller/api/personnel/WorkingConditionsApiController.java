package com.kyouseipro.neo.controller.api.personnel;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.dto.ApiResponse;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.service.personnel.WorkingConditionsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkingConditionsApiController {
    private final WorkingConditionsService workingConditionsService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/working_conditions/get/id")
	@ResponseBody
    // public Optional<WorkingConditionsEntity> getById(@RequestParam int id) {
    //         return workingConditionsService.getById(id);
    // }
    public ResponseEntity<WorkingConditionsEntity> getById(@RequestParam int id) {
            return ResponseEntity.ok(workingConditionsService.getById(id).orElse(null));
    }

    /**
     * EmployeeIDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/working_conditions/get/employeeid")
	@ResponseBody
    // public Optional<WorkingConditionsEntity> getByEmployeeId(@RequestParam int id) {
    //         return workingConditionsService.getByEmployeeId(id);
    // }
    public ResponseEntity<WorkingConditionsEntity> getByEmployeeId(@RequestParam int id) {
            return ResponseEntity.ok(workingConditionsService.getByEmployeeId(id).orElse(null));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/api/working_conditions/save")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> save(@RequestBody WorkingConditionsEntity entity, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     Integer id = workingConditionsService.save(entity, userName);
    //     if (id != null && id > 0) {
    //         historyService.save(userName, "working_conditions", "保存", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    //     } else {
    //         historyService.save(userName, "working_conditions", "保存", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody WorkingConditionsEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workingConditionsService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/working_conditions/delete")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     Integer id = workingConditionsService.deleteByIds(ids, userName);
    //     if (id != null && id > 0) {
    //         historyService.save(userName, "working_conditions", "削除", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    //     } else {
    //         historyService.save(userName, "working_conditions", "削除", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int id = workingConditionsService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/working_conditions/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        // historyService.save(userName, "working_conditions", "ダウンロード", 0, "");
        return workingConditionsService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}