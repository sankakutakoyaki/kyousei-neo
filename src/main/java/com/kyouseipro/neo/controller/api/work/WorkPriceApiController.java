package com.kyouseipro.neo.controller.api.work;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.dto.ApiResponse;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.work.WorkPriceEntity;
import com.kyouseipro.neo.service.work.WorkPriceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkPriceApiController {
    private final WorkPriceService workPriceService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/work/price/get/id")
	@ResponseBody
    // public Optional<WorkPriceEntity> getById(@RequestParam int id) {
    //     return workPriceService.getById(id);
    // }
    public ResponseEntity<WorkPriceEntity> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(workPriceService.getById(req.getId()).orElse(null));
    }

    /**
     * company_idで料金表を取得する
     * @param id
     * @return
     */
    @PostMapping("/api/work/price/get/list/companyid")
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
    @PostMapping("/api/work/price/save")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> saveWorkPrice(@RequestBody WorkPriceEntity entity, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     Integer id = workPriceService.save(entity, userName);
    //     if (id != null && id > 0) {
    //         historyService.save(userName, "work_prices", "保存", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    //     } else {
    //         historyService.save(userName, "work_prices", "保存", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> saveWorkPrice(@RequestBody WorkPriceEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = workPriceService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    // /**
    //  * IDリストのEntityを削除する
    //  * @param IDS
    //  * @return 
    //  */
    // @PostMapping("/api/work/price/delete")
	// @ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> deleteWorkPriceByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     Integer id = workPriceService.deleteWorkPriceByIds(ids, userName);
    //     if (id != null && id > 0) {
    //         historyService.saveHistory(userName, "work_prices", "削除", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    //     } else {
    //         historyService.saveHistory(userName, "work_prices", "削除", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
    //     }
    // }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/work/price/download/csv")
	@ResponseBody
    // public String downloadCsvEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     historyService.save(userName, "work_prices", "ダウンロード", 0, "");
    //     return workPriceService.downloadCsvByIds(ids);
    // }
    public String downloadCsvEntityByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return workPriceService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}
