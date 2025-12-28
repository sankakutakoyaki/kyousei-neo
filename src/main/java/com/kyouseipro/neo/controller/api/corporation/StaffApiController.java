package com.kyouseipro.neo.controller.api.corporation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.service.corporation.StaffService;
import com.kyouseipro.neo.service.document.HistoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StaffApiController {
    private final StaffService staffService;
    private final HistoryService historyService;
    
    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/staff/get/id")
	@ResponseBody
    public StaffEntity getById(@RequestParam int id) {
        return staffService.getById(id);
    }
   
    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/staff/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody StaffEntity entity, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = staffService.save(entity, userName);
        if (id != null && id > 0) {
            historyService.save(userName, "staffs", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.save(userName, "staffs", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/staff/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = staffService.deleteByIds(ids, userName);
        if (id != null && id > 0) {
            historyService.save(userName, "staffs", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        } else {
            historyService.save(userName, "staffs", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/staff/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        historyService.save(userName, "staffs", "ダウンロード", 0, "");
        return staffService.downloadCsvByIds(ids, userName);
    }
}