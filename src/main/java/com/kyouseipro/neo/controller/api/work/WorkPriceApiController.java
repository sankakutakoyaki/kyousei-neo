package com.kyouseipro.neo.controller.api.work;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.dto.ApiResponse;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.work.WorkPriceEntity;
import com.kyouseipro.neo.service.work.WorkPriceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/work")
public class WorkPriceApiController {
    private final WorkPriceService workPriceService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/price/get/id")
	@ResponseBody
    public ResponseEntity<WorkPriceEntity> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(workPriceService.getById(req.getId()).orElse(null));
    }

    /**
     * company_idで料金表を取得する
     * @param id
     * @return
     */
    @PostMapping("/price/get/list/companyid")
	@ResponseBody
    public List<WorkPriceEntity> getEntityByCompanyId(@RequestBody IdRequest req) {
        return workPriceService.getListByCompanyId(req.getId());
    }

    /**
     * 
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/price/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveWorkPrice(@RequestBody WorkPriceEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workPriceService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/price/download/csv")
	@ResponseBody
    public String downloadCsvEntityByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return workPriceService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}
