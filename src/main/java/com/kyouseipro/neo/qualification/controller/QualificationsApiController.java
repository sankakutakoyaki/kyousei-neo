package com.kyouseipro.neo.qualification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.dto.IdPairRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.qualification.entity.QualificationsEntity;
import com.kyouseipro.neo.qualification.service.QualificationsService;

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
    public SimpleResponse<List<QualificationsEntity>> getById(@RequestBody IdPairRequest req) {
        switch (req.getSecondaryId()) {
            case 0:
                return new SimpleResponse<>(null, qualificationsService.getByEmployeeId(req.getPrimaryId()));
            default:
                return new SimpleResponse<>(null, qualificationsService.getByCompanyId(req.getPrimaryId()));
        }
    }

    /**
     * 情報を保存する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/qualifications/save")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> save(@RequestBody QualificationsEntity entity, @AuthenticationPrincipal OidcUser principal) {
        Integer id = qualificationsService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(new SimpleResponse<>("保存しました。", id));
    }

    /**
     * 指定したIDのEntityを削除する
     * @param ID
     * @return 
     */
    @PostMapping("/api/qualifications/delete/id")
    @ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> deleteById(@RequestBody IdRequest req, @AuthenticationPrincipal OidcUser principal) {
        Integer result = qualificationsService.deleteById(req.getId(), principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(new SimpleResponse<>("削除しました。", result));
    }

    /**
     * すべての資格情報を取得する
     * @return
     */
    @GetMapping("/api/qualifications/get")
	@ResponseBody
    public SimpleResponse<List<QualificationsEntity>> getListForEmployee() {
        return new SimpleResponse<>(null, qualificationsService.getListForEmployee());
    }

    /**
     * すべての許認可情報を取得する
     * @return
     */
    @GetMapping("/api/license/get")
	@ResponseBody
    public SimpleResponse<List<QualificationsEntity>> getListForCompany() {
        return new SimpleResponse<>(null, qualificationsService.getListFroCompany());
    }
}
