package com.kyouseipro.neo.controller.api.recycle;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.dto.NumberRequest;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.service.recycle.RecycleMakerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecycleMakerApiController extends BaseController {
    private final RecycleMakerService recycleMakerService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/recycle/maker/get/id")
	@ResponseBody
    // public ResponseEntity getById(@RequestParam int id) {
    //     RecycleMakerEntity entity = recycleMakerService.getById(id);
    //     if (entity != null) {
    //         return ResponseEntity.ok(entity);
    //     } else {
    //         return ResponseEntity.ofNullable(null);
    //     }
    // }
    public ResponseEntity<RecycleMakerEntity> getById(@RequestBody IdRequest req) {
        // return recycleMakerService.getById(id)
        //     .map(ResponseEntity::ok)
        //     .orElseGet(() -> ResponseEntity.notFound().build());
        return ResponseEntity.ok(recycleMakerService.getById(req.getId()).orElse(null));
    }
   
    /**
     * CodeからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/recycle/maker/get/code")
	@ResponseBody
    // public ResponseEntity getByCode(@RequestParam int code) {
    //     RecycleMakerEntity entity = recycleMakerService.getByCode(code);
    //     if (entity != null) {
    //         return ResponseEntity.ok(entity);
    //     } else {
    //         return ResponseEntity.ofNullable(null);
    //     }
    // }
    public ResponseEntity<RecycleMakerEntity> getByCode(@RequestBody NumberRequest req) {
        // return recycleMakerService.getByCode(code)
        //     .map(ResponseEntity::ok)
        //     .orElseGet(() -> ResponseEntity.notFound().build());
        return ResponseEntity.ok(recycleMakerService.getByCode(req.getNumber()).orElse(null));
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/api/recycle/maker/get/list")
	@ResponseBody
    public List<RecycleMakerEntity> getList() {
        return recycleMakerService.getList();
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/api/recycle/maker/save")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> save(@RequestBody RecycleMakerEntity entity, @AuthenticationPrincipal OidcUser principal) {
    //     // String userName = principal.getAttribute("preferred_username");
    //     Integer id = recycleMakerService.save(entity, principal.getAttribute("preferred_username"));
    //     if (id != null && id > 0) {
    //         return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    //     // historyService.save(userName, "recycle_makers", "保存", 200, "成功");
    //     } else {
    //         return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody RecycleMakerEntity entity, @AuthenticationPrincipal OidcUser principal) {
        int id = recycleMakerService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/recycle/maker/delete")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     Integer id = recycleMakerService.deleteByIds(ids);
    //     if (id != null && id > 0) {
    //         historyService.save(userName, "recycle_makers", "削除", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    //     } else {
    //         historyService.save(userName, "recycle_makers", "削除", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int id = recycleMakerService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/recycle/maker/download/csv")
	@ResponseBody
    // public String downloadCsvByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     historyService.save(userName, "recycle_makers", "ダウンロード", 0, "");
    //     return recycleMakerService.downloadCsvByIds(ids);
    // }
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return recycleMakerService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}