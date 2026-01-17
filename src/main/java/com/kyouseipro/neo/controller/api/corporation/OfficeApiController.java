package com.kyouseipro.neo.controller.api.corporation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.dto.ApiResponse;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.service.corporation.OfficeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OfficeApiController {
    private final OfficeService officeService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/office/get/id")
	@ResponseBody
    // public Optional<OfficeEntity> getById(@RequestParam int id) {
    //     return officeService.getById(id);
    // }
    public ResponseEntity<OfficeEntity> getById(@RequestParam int id) {
        return ResponseEntity.ok(officeService.getById(id).orElse(null));
    }

    /**
     * 情報を保存する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/office/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody OfficeEntity entity, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        int id = officeService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        // if (id != null && id > 0) {
        //     historyService.save(userName, "offices", "保存", 200, "成功");
        //     return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        // } else {
        //     historyService.save(userName, "offices", "保存", 400, "失敗");
        //     return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        // }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/office/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        Integer id = officeService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        // if (id != null && id > 0) {
        //     historyService.save(userName, "offices", "削除", 200, "成功");
        //     return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        // } else {
        //     historyService.save(userName, "offices", "削除", 200, "失敗");
        //     return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        // }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/office/download/csv")
	@ResponseBody
    // public String downloadCsvByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        // historyService.save(userName, "offices", "ダウンロード", 0, "");
        return officeService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}