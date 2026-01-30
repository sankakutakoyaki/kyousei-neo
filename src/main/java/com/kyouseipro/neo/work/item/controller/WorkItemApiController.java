package com.kyouseipro.neo.work.item.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.dto.ApiResponse;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.work.item.entity.WorkItemEntity;
import com.kyouseipro.neo.work.item.service.WorkItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/work")
public class WorkItemApiController {
    private final WorkItemService workItemService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/item/get/id")
	@ResponseBody
    public ResponseEntity<WorkItemEntity> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(workItemService.getById(req.getId()).orElse(null));
    }

    /**
     * 
     * @param id
     * @return
     */
    @PostMapping("/item/get/category")
	@ResponseBody
    public List<WorkItemEntity> getByCategoryId(@RequestBody IdRequest req) {
        return workItemService.getByCategoryId(req.getId());
    }

    /**
     * Listを取得する
     * @return
     */
    @GetMapping("/item/get/list")
	@ResponseBody
    public List<WorkItemEntity> getList() {
        return workItemService.getList();
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/item/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody WorkItemEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workItemService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/item/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workItemService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/item/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return workItemService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}
