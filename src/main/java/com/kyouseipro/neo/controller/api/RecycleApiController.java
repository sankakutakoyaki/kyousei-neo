package com.kyouseipro.neo.controller.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleDateEntity;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.recycle.RecycleService;

@Controller
@RequiredArgsConstructor
public class RecycleApiController {
    private final RecycleService recycleService;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    /**
     * 
     * @param str
     * @return
     */
    @PostMapping("/recycle/exists/number")
    @ResponseBody
    public ResponseEntity existsEntityByNumbe(@RequestParam String num) {
        RecycleEntity entity = recycleService.existsRecycleByNumber(num);
        if (entity != null) {
            return ResponseEntity.ok(entity);
        } else {
            return ResponseEntity.ofNullable(null);
        }
    }

    /**
     * 
     * @param start
     * @param end
     * @param col
     * @return
     */
    @PostMapping("/recycle/get/between")
    @ResponseBody
    public List<RecycleEntity> getBetweenEntity(@RequestParam LocalDate start, @RequestParam LocalDate end, @RequestParam String col) {
        List<RecycleEntity> list = recycleService.getBetweenRecycleEntity(start, end, col);
        return list;
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/recycle/save/{type}")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveRecycleItem(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = 0;
        switch (type) {
            case "regist":
                List<RecycleEntity> itemList1 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleEntity>>() {});
                id = recycleService.saveRecycle(itemList1, userName);
                break;
            case "delivery":
                List<RecycleDateEntity> itemList2 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleDateEntity>>() {});
                id = recycleService.updateRecycleDate(itemList2, userName, type);
                break;
            case "shipping":
                List<RecycleDateEntity> itemList3 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleDateEntity>>() {});
                id = recycleService.updateRecycleDate(itemList3, userName, type);
                break;
            case "loss":
                List<RecycleDateEntity> itemList4 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleDateEntity>>() {});
                id = recycleService.updateRecycleDate(itemList4, userName, type);
                break;
            case "edit":
                List<RecycleEntity> itemList5 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleEntity>>() {});
                id = recycleService.saveRecycle(itemList5, userName);
                break;
            default:
                break;
        }
        if (id != null && id > 0) {
            historyService.saveHistory(userName, "recycles", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.saveHistory(userName, "recycles", "保存", 400, "失敗");
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
        String userName = principal.getAttribute("preferred_userName");
        Integer num = recycleService.deleteRecycleByIds(ids, userName);
        if (num != null && num > 0) {
            historyService.saveHistory(userName, "recycels", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok(num + "件削除しました。", num));
        } else {
            historyService.saveHistory(userName, "recycels", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました。"));
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
        historyService.saveHistory(userName, "recycles", "ダウンロード", 0, "");
        return recycleService.downloadCsvRecycleByIds(ids, userName);
    }
}
