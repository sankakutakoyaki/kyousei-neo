package com.kyouseipro.neo.recycle.item.controller;

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
import com.kyouseipro.neo.dto.NumberRequest;
import com.kyouseipro.neo.recycle.item.entity.RecycleItemEntity;
import com.kyouseipro.neo.recycle.item.service.RecycleItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/recycle")
public class RecycleItemApiController {
    private final RecycleItemService recycleItemService;
    
    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/item/get/id")
	@ResponseBody
    public ResponseEntity<RecycleItemEntity> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(recycleItemService.getById(req.getId()).orElse(null));
    }

    /**
     * CodeからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/item/get/code")
	@ResponseBody
    public ResponseEntity<RecycleItemEntity> getByCode(@RequestBody NumberRequest req) {
        return ResponseEntity.ok(recycleItemService.getByCode(req.getNumber()).orElse(null));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/item/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody RecycleItemEntity entity, @AuthenticationPrincipal OidcUser principal) {
        int id = recycleItemService.save(entity, principal.getAttribute("preferred_username"));
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
        int id = recycleItemService.deleteByIds(ids, principal.getAttribute("preferred_username"));
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
        return recycleItemService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}