package com.kyouseipro.neo.work.item.controller;

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

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.work.item.entity.WorkItemEntity;
import com.kyouseipro.neo.work.item.service.WorkItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/work/item")
public class WorkItemApiController {
    private final WorkItemService workItemService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/id")
	@ResponseBody
    public ResponseEntity<SimpleResponse<WorkItemEntity>> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(workItemService.getById(req.getId())));
    }

    /**
     * 
     * @param id
     * @return
     */
    @PostMapping("/get/category")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<WorkItemEntity>>> getByCategoryId(@RequestBody IdRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(workItemService.getByCategoryId(req.getId())));
    }

    /**
     * Listを取得する
     * @return
     */
    @GetMapping("/get/list")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<WorkItemEntity>>> getList() {
        return ResponseEntity.ok(SimpleResponse.ok(workItemService.getList()));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/save")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> save(@RequestBody WorkItemEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workItemService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/delete")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workItemService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    // @PostMapping("/item/download/csv")
	// @ResponseBody
    // public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
    //     return workItemService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    // }
    @PostMapping(value = "/download/csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        String csv = workItemService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv";

        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }
}
