package com.kyouseipro.neo.controller.api.recycle;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.recycle.RecycleMakerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecycleMakerApiController {
    private final RecycleMakerService recycleMakerService;
    private final HistoryService historyService;
    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/recycle/maker/get/id")
	@ResponseBody
    public ResponseEntity getById(@RequestParam int id) {
        RecycleMakerEntity entity = recycleMakerService.getById(id);
        if (entity != null) {
            return ResponseEntity.ok(entity);
        } else {
            return ResponseEntity.ofNullable(null);
        }
    }

    /**
     * CodeからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/recycle/maker/get/code")
	@ResponseBody
    public ResponseEntity getByCode(@RequestParam int code) {
        RecycleMakerEntity entity = recycleMakerService.getByCode(code);
        if (entity != null) {
            return ResponseEntity.ok(entity);
        } else {
            return ResponseEntity.ofNullable(null);
        }
    }
    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/recycle/maker/get/list")
	@ResponseBody
    public List<RecycleMakerEntity> getList() {
        return recycleMakerService.getList();
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/recycle/maker/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody RecycleMakerEntity entity, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        Integer id = recycleMakerService.save(entity, principal.getAttribute("preferred_username"));
        // historyService.save(userName, "recycle_makers", "保存", 200, "成功");
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/recycle/maker/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = recycleMakerService.deleteByIds(ids);
        if (id != null && id > 0) {
            historyService.save(userName, "recycle_makers", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        } else {
            historyService.save(userName, "recycle_makers", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/recycle/maker/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        historyService.save(userName, "recycle_makers", "ダウンロード", 0, "");
        return recycleMakerService.downloadCsvByIds(ids);
    }
}