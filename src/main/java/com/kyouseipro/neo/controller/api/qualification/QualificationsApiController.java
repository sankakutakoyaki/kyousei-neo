package com.kyouseipro.neo.controller.api.qualification;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.qualification.QualificationsEntity;
import com.kyouseipro.neo.service.qualification.QualificationsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class QualificationsApiController {
    private final QualificationsService qualificationsService;

    /**
     * IDから情報を取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/qualifications/get/id")
	@ResponseBody
    public List<QualificationsEntity> getById(@RequestParam int id, @RequestParam int category) {
        switch (category) {
            case 0:
                return qualificationsService.getByEmployeeId(id);
            default:
                return qualificationsService.getByCompanyId(id);
        }
    }

    /**
     * 情報を保存する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/qualifications/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody QualificationsEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = qualificationsService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        // if (id != null && id > 0) {
        //     return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        // } else {
        //     return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        // }
    }

    /**
     * 指定したIDのEntityを削除する
     * @param ID
     * @return 
     */
    @PostMapping("/api/qualifications/delete/id")
    @ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> deleteById(@RequestParam int id, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     Integer result = qualificationsService.deleteById(id, userName);
    //     if (result != null && result > 0) {
    //         historyService.save(userName, "qualifications", "削除", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok("削除しました。", result));
    //     } else {
    //         historyService.save(userName, "qualifications", "削除", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> deleteById(@RequestParam int id, @AuthenticationPrincipal OidcUser principal) {
        Integer result = qualificationsService.deleteById(id, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("削除しました。", result));
    }

    /**
     * すべての資格情報を取得する
     * @return
     */
    @GetMapping("/api/qualifications/get")
	@ResponseBody
    public List<QualificationsEntity> getListForEmployee() {
        return qualificationsService.getListForEmployee();
    }

    /**
     * すべての許認可情報を取得する
     * @return
     */
    @GetMapping("/api/license/get")
	@ResponseBody
    public List<QualificationsEntity> getListForCompany() {
        return qualificationsService.getListFroCompany();
    }
}
