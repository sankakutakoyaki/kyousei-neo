package com.kyouseipro.neo.controller.api.work;

import java.util.List;
import java.util.Optional;

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
import com.kyouseipro.neo.entity.work.WorkItemEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.work.WorkItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkItemApiController {
    private final WorkItemService workItemService;
    private final HistoryService historyService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/work/item/get/id")
	@ResponseBody
    public Optional<WorkItemEntity> getById(@RequestParam int id) {
        return workItemService.getById(id);
    }

    /**
     * 
     * @param id
     * @return
     */
    @PostMapping("/api/work/item/get/category")
	@ResponseBody
    public List<WorkItemEntity> getByCategoryId(@RequestParam int id) {
        return workItemService.getByCategoryId(id);
    }

    /**
     * Listを取得する
     * @return
     */
    @GetMapping("/api/work/item/get/list")
	@ResponseBody
    public List<WorkItemEntity> getList() {
        return workItemService.getList();
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/api/work/item/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody WorkItemEntity entity, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = workItemService.save(entity, userName);
        if (id != null && id > 0) {
            historyService.save(userName, "work_items", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.save(userName, "work_items", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/work/item/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = workItemService.deleteByIds(ids, userName);
        if (id != null && id > 0) {
            historyService.save(userName, "work_items", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        } else {
            historyService.save(userName, "work_items", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/work/item/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        historyService.save(userName, "work_items", "ダウンロード", 0, "");
        return workItemService.downloadCsvByIds(ids);
    }
}
