package com.kyouseipro.neo.recycle.maker.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
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
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.common.validation.service.ValidateService;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.dto.NumberRequest;
import com.kyouseipro.neo.recycle.maker.entity.RecycleMakerEntity;
import com.kyouseipro.neo.recycle.maker.service.RecycleMakerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/recycle/maker")
public class RecycleMakerApiController extends BaseController {
    private final RecycleMakerService recycleMakerService;
    private final ValidateService validateService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/id")
	@ResponseBody
    public ResponseEntity<SimpleResponse<RecycleMakerEntity>> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(new SimpleResponse<>(null, recycleMakerService.getById(req.getId())));
    }
   
    /**
     * CodeからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/code")
	@ResponseBody
    public ResponseEntity<SimpleResponse<RecycleMakerEntity>> getByCode(@RequestBody NumberRequest req) {
        return ResponseEntity.ok(new SimpleResponse<>(null, recycleMakerService.getByCode(req.getNumber())));
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/get/list")
	@ResponseBody
    public SimpleResponse<List<RecycleMakerEntity>> getList() {
        return new SimpleResponse<>(null, recycleMakerService.getList());
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/save")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> save(@RequestBody RecycleMakerEntity entity, @AuthenticationPrincipal OidcUser principal) {
        validateService.validateCodeAbbrRule(entity.getCode(), entity.getAbbrName());

        int id = recycleMakerService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(new SimpleResponse<>("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("delete")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int id = recycleMakerService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(new SimpleResponse<>(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    // @PostMapping("/maker/download/csv")
	// @ResponseBody
    // public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
    //     return recycleMakerService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    // }
    @PostMapping(value = "/download/csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        String csv = recycleMakerService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv";

        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }
}