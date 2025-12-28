package com.kyouseipro.neo.controller.api.personnel;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.WorkingConditionsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkingConditionsApiController {
    private final WorkingConditionsService workingConditionsService;
    private final HistoryService historyService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/working_conditions/get/id")
	@ResponseBody
    public WorkingConditionsEntity getById(@RequestParam int id) {
            return workingConditionsService.getById(id);
    }

    /**
     * EmployeeIDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/working_conditions/get/employeeid")
	@ResponseBody
    public WorkingConditionsEntity getByEmployeeId(@RequestParam int id) {
            return workingConditionsService.getByEmployeeId(id);
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/working_conditions/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody WorkingConditionsEntity entity, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = workingConditionsService.save(entity, userName);
        if (id != null && id > 0) {
            historyService.save(userName, "working_conditions", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.save(userName, "working_conditions", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/working_conditions/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = workingConditionsService.deleteByIds(ids, userName);
        if (id != null && id > 0) {
            historyService.save(userName, "working_conditions", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        } else {
            historyService.save(userName, "working_conditions", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/working_conditions/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        historyService.save(userName, "working_conditions", "ダウンロード", 0, "");
        return workingConditionsService.downloadCsvByIds(ids, userName);
    }
}