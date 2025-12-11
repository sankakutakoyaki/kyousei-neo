package com.kyouseipro.neo.controller.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.recycle.RecycleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecycleApiController {
    private final RecycleService recycleService;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/recycle/get/id")
	@ResponseBody
    public ResponseEntity getEntityById(@RequestParam int id) {
        RecycleEntity entity = recycleService.getRecycleById(id);
        if (entity != null) {
            return ResponseEntity.ok(entity);
        } else {
            return ResponseEntity.ofNullable(null);
        }
    }

        
    /**
     * NumberからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/recycle/exists/number")
	@ResponseBody
    public ResponseEntity getEntityByNumber(@RequestParam String number) {
        RecycleEntity entity = recycleService.existsRecycleByNumber(number);
        if (entity != null) {
            return ResponseEntity.ok(entity);
        } else {
            return ResponseEntity.ofNullable(null);
        }
    }

    /**
     * 期間内の商品情報を取得する
     * @param start
     * @param end
     * @return
     */
    @PostMapping("/recycle/get/between")
	@ResponseBody
    public List<RecycleEntity> getBetweenAllEntity(
                @RequestParam LocalDate start,
                @RequestParam LocalDate end,
                @RequestParam String col) {
        List<RecycleEntity> list = recycleService.getBetweenRecycleEntity(start, end, col);
        return list;
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/recycle/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveRecycle(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        List<RecycleEntity> itemList = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleEntity>>() {});
        Integer id = recycleService.saveRecycle(itemList, userName);
        if (id != null && id > 0) {
            historyService.saveHistory(userName, "orders", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.saveHistory(userName, "orders", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/recycle/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = recycleService.deleteRecycleByIds(ids, userName);
        if (id != null && id > 0) {
            historyService.saveHistory(userName, "order_items", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        } else {
            historyService.saveHistory(userName, "order_items", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/recycle/download/csv")
	@ResponseBody
    public String downloadCsvEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        historyService.saveHistory(userName, "orders_item", "ダウンロード", 0, "");
        return recycleService.downloadCsvRecycleByIds(ids, userName);
    }
}
