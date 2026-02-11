package com.kyouseipro.neo.recycle.regist.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.dto.ApiResponse;
import com.kyouseipro.neo.dto.BetweenRequest;
import com.kyouseipro.neo.dto.IdDateRequest;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.dto.StringRequest;
import com.kyouseipro.neo.recycle.regist.entity.RecycleDateEntity;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntity;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntityRequest;
import com.kyouseipro.neo.recycle.regist.service.RecycleService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/recycle")
public class RecycleApiController {
    private final RecycleService recycleService;
    private final ObjectMapper objectMapper;

    // /**
    //  * IDからEntityを取得する
    //  * @param ID
    //  * @return 
    //  */
    // @PostMapping("/get/id")
	// @ResponseBody
    // public ResponseEntity<RecycleEntity> getById(@RequestBody IdRequest req) {
    //     return ResponseEntity.ok(recycleService.getById(req.getId()).orElse(null));
    // }

    // /**
    //  * 
    //  * @param str
    //  * @return
    //  */
    // @PostMapping("/exists/number")
    // @ResponseBody
    // public ResponseEntity<RecycleEntity> findByNumber(@RequestBody StringRequest str) {
    //     return ResponseEntity.ok(recycleService.existsByNumber(str.getValue()).orElse(null));
    // }

    /**
     * 
     * @param start
     * @param end
     * @param col
     * @return
     */
    @PostMapping("/get/number")
    @ResponseBody
    public ResponseEntity<RecycleEntity> findByNumber(@RequestBody StringRequest req) {
        return ResponseEntity.ok(recycleService.getByNumber(req.getValue()).orElse(null));
    }

    /**
     * 
     * @param start
     * @param end
     * @param col
     * @return
     */
    @PostMapping("/get/between")
    @ResponseBody
    public List<RecycleEntity> getBetween(@RequestBody BetweenRequest req) {
        return recycleService.getBetween(req.getStart(), req.getEnd(), req.getType());
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/save/{type}")
	@ResponseBody
    public ResponseEntity<RecycleEntity> save(@RequestBody RecycleEntity entity, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {
        String userName = principal.getAttribute("preferred_username");
        switch (type) {
            case "regist":
                // List<RecycleEntity> itemList1 = objectMapper.convertValue(body.get("list"), new TypeReference<List<RecycleEntity>>() {});
                // id = recycleService.save(entity, userName);
                return ResponseEntity.ok(recycleService.save(entity, userName).orElse(null));
                // break;
            case "delivery":
            case "shipping":
            case "loss":
            case "edit":
                // RecycleEntity entity = objectMapper.convertValue(body.get("entity"), new TypeReference<RecycleEntity>() {});
                // id = recycleService.update(entity, userName);
                return ResponseEntity.ok(recycleService.update(entity, type, userName).orElse(null));
                // break;
            default:
                return null;
                // break;
        }
        // return ResponseEntity.ok(ApiResponse.ok("保存しました。", entity));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    // @PostMapping("/update/{type}")
	// @ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> update(@RequestBody RecycleDateEntity req, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {
    //     String userName = principal.getAttribute("preferred_username");
    //     int id = recycleService.updateForDate(req, userName, type);
    //     return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    // }
    @PostMapping("/update")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> update(@RequestBody RecycleEntityRequest req) {
        int id = recycleService.bulkUpdate(req);
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }
    
    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int num = recycleService.deleteByIds(ids, principal.getAttribute("preferred_userName"));
        return ResponseEntity.ok(ApiResponse.ok(num + "件削除しました。", num));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return recycleService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}
