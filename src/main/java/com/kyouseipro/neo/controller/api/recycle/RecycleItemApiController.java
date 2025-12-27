package com.kyouseipro.neo.controller.api.recycle;

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
import com.kyouseipro.neo.entity.recycle.RecycleItemEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.management.recycle.RecycleItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecycleItemApiController {
    private final RecycleItemService recycleItemService;
    private final HistoryService historyService;
    
    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/recycle/item/get/id")
	@ResponseBody
    public ResponseEntity getEntityById(@RequestParam int id) {
        // return recycleItemService.getRecycleItemById(sql, id);
        RecycleItemEntity entity = recycleItemService.getRecycleItemById(id);
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
    @PostMapping("/recycle/item/get/code")
	@ResponseBody
    public ResponseEntity getEntityByCode(@RequestParam int code) {
        // return recycleItemService.getRecycleItemByCode(sql, code);
        RecycleItemEntity entity = recycleItemService.getRecycleItemByCode(code);
        if (entity != null) {
            return ResponseEntity.ok(entity);
        } else {
            return ResponseEntity.ofNullable(null);
        }
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/recycle/item/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveRecycleItem(@RequestBody RecycleItemEntity entity, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = recycleItemService.saveRecycleItem(entity);
        if (id != null && id > 0) {
            historyService.saveHistory(userName, "recycle_items", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.saveHistory(userName, "recycle_items", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/recycle/item/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = recycleItemService.deleteRecycleItemByIds(ids);
        if (id != null && id > 0) {
            historyService.saveHistory(userName, "recycle_items", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        } else {
            historyService.saveHistory(userName, "recycle_items", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/recycle/item/download/csv")
	@ResponseBody
    public String downloadCsvEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        historyService.saveHistory(userName, "recycle_items", "ダウンロード", 0, "");
        return recycleItemService.downloadCsvRecycleItemByIds(ids);
    }
}