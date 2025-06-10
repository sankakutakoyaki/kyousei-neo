package com.kyouseipro.neo.controller.api;

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
    @PostMapping("/qualifications/get/id")
	@ResponseBody
    public QualificationsEntity getQualificationsById(@RequestParam int id) {
        return qualificationsService.getQualificationsById(id);
    }

    /**
     * 情報を保存する
     * @param IDS
     * @return 
     */
    @PostMapping("/qualifications/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveEntity(@RequestBody QualificationsEntity entity, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = qualificationsService.saveQualifications(entity, userName);
        if (id != null && id > 0) {
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * 指定したIDのEntityを削除する
     * @param ID
     * @return 
     */
    @PostMapping("/qualifications/delete/id")
    @ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteEntityById(@RequestParam int id, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer result = qualificationsService.deleteQualificationsById(id, userName);
        if (result != null && result > 0) {
            return ResponseEntity.ok(ApiResponse.ok("削除しました。", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * すべての資格情報を取得する
     * @return
     */
    @GetMapping("/qualifications/get/employee")
	@ResponseBody
    public List<QualificationsEntity> getQualificationsListForEmployee() {
        return qualificationsService.getEmployeeQualificationsList();
    }

    /**
     * すべての資格情報を取得する
     * @return
     */
    @GetMapping("/qualifications/get/company")
	@ResponseBody
    public List<QualificationsEntity> getQualificationsListForCompany() {
        return qualificationsService.getCompanyQualificationsList();
    }
}
