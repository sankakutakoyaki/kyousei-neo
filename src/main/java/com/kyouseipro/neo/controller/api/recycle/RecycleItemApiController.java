package com.kyouseipro.neo.controller.api.recycle;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.recycle.RecycleItemEntity;
import com.kyouseipro.neo.service.recycle.RecycleItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecycleItemApiController {
    private final RecycleItemService recycleItemService;
    
    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/recycle/item/get/id")
	@ResponseBody
    // public ResponseEntity getById(@RequestParam int id) {
    //     RecycleItemEntity entity = recycleItemService.getById(id);
    //     if (entity != null) {
    //         return ResponseEntity.ok(entity);
    //     } else {
    //         return ResponseEntity.ofNullable(null);
    //     }
    // }
    public ResponseEntity<RecycleItemEntity> getById(@RequestParam int id) {

        // return recycleItemService.getById(id)
        //     .map(ResponseEntity::ok)
        //     .orElseGet(() -> ResponseEntity.notFound().build());
        return ResponseEntity.ok(recycleItemService.getById(id).orElse(null));
    }

    /**
     * CodeからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/recycle/item/get/code")
	@ResponseBody
    // public ResponseEntity getByCode(@RequestParam int code) {
    //     RecycleItemEntity entity = recycleItemService.getByCode(code);
    //     if (entity != null) {
    //         return ResponseEntity.ok(entity);
    //     } else {
    //         return ResponseEntity.ofNullable(null);
    //     }
    // }
    public ResponseEntity<RecycleItemEntity> getByCode(@RequestParam int code) {
        // return recycleItemService.getByCode(code)
        //     .map(ResponseEntity::ok)
        //     .orElseGet(() -> ResponseEntity.notFound().build());
        return ResponseEntity.ok(recycleItemService.getByCode(code).orElse(null));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/api/recycle/item/save")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> save(@RequestBody RecycleItemEntity entity, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     Integer id = recycleItemService.save(entity);
    //     if (id != null && id > 0) {
    //         historyService.save(userName, "recycle_items", "保存", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    //     } else {
    //         historyService.save(userName, "recycle_items", "保存", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody RecycleItemEntity entity, @AuthenticationPrincipal OidcUser principal) {
        int id = recycleItemService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/recycle/item/delete")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     Integer id = recycleItemService.deleteByIds(ids);
    //     if (id != null && id > 0) {
    //         historyService.save(userName, "recycle_items", "削除", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    //     } else {
    //         historyService.save(userName, "recycle_items", "削除", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int id = recycleItemService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/recycle/item/download/csv")
	@ResponseBody
    // public String downloadCsvByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     historyService.save(userName, "recycle_items", "ダウンロード", 0, "");
    //     return recycleItemService.downloadCsvByIds(ids);
    // }
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return recycleItemService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}