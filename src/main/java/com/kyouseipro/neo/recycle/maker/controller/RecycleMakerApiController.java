package com.kyouseipro.neo.recycle.maker.controller;

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

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.common.validation.service.ValidateService;
import com.kyouseipro.neo.dto.ApiResponse;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.dto.NumberRequest;
import com.kyouseipro.neo.recycle.maker.entity.RecycleMakerEntity;
import com.kyouseipro.neo.recycle.maker.service.RecycleMakerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/recycle")
public class RecycleMakerApiController extends BaseController {
    private final RecycleMakerService recycleMakerService;
    private final ValidateService validateService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/maker/get/id")
	@ResponseBody
    public ResponseEntity<RecycleMakerEntity> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(recycleMakerService.getById(req.getId()).orElse(null));
    }
   
    /**
     * CodeからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/maker/get/code")
	@ResponseBody
    public ResponseEntity<RecycleMakerEntity> getByCode(@RequestBody NumberRequest req) {
        return ResponseEntity.ok(recycleMakerService.getByCode(req.getNumber()).orElse(null));
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/maker/get/list")
	@ResponseBody
    public List<RecycleMakerEntity> getList() {
        return recycleMakerService.getList();
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/maker/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody RecycleMakerEntity entity, @AuthenticationPrincipal OidcUser principal) {
        validateService.validateCodeAbbrRule(entity.getCode(), entity.getAbbrName());

        int id = recycleMakerService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/maker/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int id = recycleMakerService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/maker/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return recycleMakerService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}