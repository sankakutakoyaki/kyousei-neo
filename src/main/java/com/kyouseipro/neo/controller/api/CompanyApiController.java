package com.kyouseipro.neo.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.service.corporation.CompanyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CompanyApiController {
    private final CompanyService companyService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/company/get/id")
	@ResponseBody
    public CompanyEntity getEntityById(@RequestParam int id) {
        return companyService.getCompanyById(id);
    }


    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/company/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveEntity(@RequestBody CompanyEntity entity, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = companyService.saveCompany(entity, userName);
        if (id != null && id > 0) {
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/company/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = companyService.deleteCompanyByIds(ids, userName);
        if (id != null && id > 0) {
            return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/company/download/csv")
	@ResponseBody
    public String downloadCsvEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        return companyService.downloadCsvCompanyByIds(ids, userName);
    }

    // /**
    //  * すべてのコンボボックス用取引先情報を取得する
    //  * @return
    //  */
    // @GetMapping("/company/get/combo")
	// @ResponseBody
    // public List<CompanyEntity> getCompanyCombo() {
    //     return comboBoxService.getClient();
    // }
}