package com.kyouseipro.neo.controller.api.recycle;

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
import com.kyouseipro.neo.entity.dto.BetweenRequest;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.dto.StringRequest;
import com.kyouseipro.neo.entity.recycle.RecycleDateEntity;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;
import com.kyouseipro.neo.service.recycle.RecycleService;

@Controller
@RequiredArgsConstructor
public class RecycleApiController {
    private final RecycleService recycleService;
    private final ObjectMapper objectMapper;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/recycle/get/id")
	@ResponseBody
    // public ResponseEntity getById(@RequestParam int id) {
    //     RecycleEntity entity = recycleService.getById(id);
    //     if (entity != null) {
    //         return ResponseEntity.ok(entity);
    //     } else {
    //         return ResponseEntity.ofNullable(null);
    //     }
    // }
    public ResponseEntity<RecycleEntity> getById(@RequestParam int id) {

        // return recycleService.getById(id)
        //     .map(ResponseEntity::ok)
        //     .orElseGet(() -> ResponseEntity.notFound().build());
        return ResponseEntity.ok(recycleService.getById(id).orElse(null));
    }

    /**
     * 
     * @param str
     * @return
     */
    @PostMapping("/api/recycle/exists/number")
    @ResponseBody
    // public Optional<ResponseEntity> existsByNmbe(@RequestParam String num) {
    //     RecycleEntity entity = recycleService.existsByNumber(num);
    //     if (entity != null) {
    //         return ResponseEntity.ok(entity);
    //     } else {
    //         return ResponseEntity.ofNullable(null);
    //     }
    // }
    public ResponseEntity<RecycleEntity> findByNumber(@RequestBody StringRequest str) {

        // return recycleService.existsByNumber(num)
        //     .map(ResponseEntity::ok)
        //     .orElseGet(() -> ResponseEntity.notFound().build());
        return ResponseEntity.ok(recycleService.existsByNumber(str.getValue()).orElse(null));
    // public ApiResponse<RecycleEntity> findByNumber(@RequestParam String num) {
    //     return recycleService.existsByNumber(num)
    //         .map(entity -> ApiResponse.ok(null, entity))
    //         .orElseGet(() -> ApiResponse.ok(null, null));
    }

    /**
     * 
     * @param start
     * @param end
     * @param col
     * @return
     */
    @PostMapping("/api/recycle/get/between")
    @ResponseBody
    // public List<RecycleEntity> getBetween(@RequestParam LocalDate start, @RequestParam LocalDate end, @RequestParam String col) {
    public List<RecycleEntity> getBetween(@RequestBody BetweenRequest req) {
        return recycleService.getBetween(req.getStart(), req.getEnd(), req.getType());
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/api/recycle/save/{type}")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = 0;
        switch (type) {
            case "regist":
                List<RecycleEntity> itemList1 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleEntity>>() {});
                id = recycleService.save(itemList1, userName);
                break;
            // case "delivery":
            //     List<RecycleDateEntity> itemList2 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleDateEntity>>() {});
            //     id = recycleService.updateForDate(itemList2, userName, type);
            //     break;
            // case "shipping":
            //     List<RecycleDateEntity> itemList3 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleDateEntity>>() {});
            //     id = recycleService.updateForDate(itemList3, userName, type);
            //     break;
            // case "loss":
            //     List<RecycleDateEntity> itemList4 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleDateEntity>>() {});
            //     id = recycleService.updateForDate(itemList4, userName, type);
            //     break;
            case "edit":
                RecycleEntity entity = objectMapper.convertValue(body.get("entity"), new TypeReference<RecycleEntity>() {});
                id = recycleService.update(entity, userName);
                break;
            default:
                List<RecycleDateEntity> itemList2 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleDateEntity>>() {});
                id = recycleService.updateForDate(itemList2, userName, type);
                break;
        }
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        // if (id != null && id > 0) {
        //     historyService.save(userName, "recycles", "保存", 200, "成功");
        //     return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        // } else {
        //     historyService.save(userName, "recycles", "保存", 400, "失敗");
        //     return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        // }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/recycle/delete")
    @ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_userName");
    //     Integer num = recycleService.deleteByIds(ids, userName);
    //     if (num != null && num > 0) {
    //         historyService.save(userName, "recycels", "削除", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok(num + "件削除しました。", num));
    //     } else {
    //         historyService.save(userName, "recycels", "削除", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました。"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int num = recycleService.deleteByIds(ids, principal.getAttribute("preferred_userName"));
        return ResponseEntity.ok(ApiResponse.ok(num + "件削除しました。", num));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/recycle/download/csv")
	@ResponseBody
    // public String downloadCsvByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     historyService.save(userName, "recycles", "ダウンロード", 0, "");
    //     return recycleService.downloadCsvByIds(ids, userName);
    // }
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return recycleService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}
