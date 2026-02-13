package com.kyouseipro.neo.work.price.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.work.price.entity.WorkPriceEntity;
import com.kyouseipro.neo.work.price.service.WorkPriceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/work/price")
public class WorkPriceApiController {
    private final WorkPriceService workPriceService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/id")
	@ResponseBody
    public ResponseEntity<SimpleResponse<WorkPriceEntity>> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(workPriceService.getById(req.getId())));
    }

    /**
     * company_idで料金表を取得する
     * @param id
     * @return
     */
    @PostMapping("/get/list/companyid")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<WorkPriceEntity>>> getEntityByCompanyId(@RequestBody IdRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(workPriceService.getListByCompanyId(req.getId())));
    }

    /**
     * 
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/save")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> saveWorkPrice(@RequestBody WorkPriceEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workPriceService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok("保存しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    // @PostMapping("/price/download/csv")
	// @ResponseBody
    // public String downloadCsvEntityByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
    //     return workPriceService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    // }
    @PostMapping(value = "/download/csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        String csv = workPriceService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv";

        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }
}
