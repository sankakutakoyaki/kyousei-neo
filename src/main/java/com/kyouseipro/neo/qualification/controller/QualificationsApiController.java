package com.kyouseipro.neo.qualification.controller;

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

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.qualification.entity.QualificationsEntity;
import com.kyouseipro.neo.qualification.entity.QualificationsEntityRequest;
import com.kyouseipro.neo.qualification.service.QualificationsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/qualifications")
public class QualificationsApiController {
    private final QualificationsService qualificationsService;

    /**
     * IDから情報を取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/id")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<QualificationsEntity>>> getByEmployeeId(@RequestBody IdRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(qualificationsService.getByEmployeeId(req.getId())));
    }

    /**
     * IDから情報を取得する
     * @param ID
     * @return 
     */
    @PostMapping("/license/get/id")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<QualificationsEntity>>> getByCompnayId(@RequestBody IdRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(qualificationsService.getByCompanyId(req.getId())));
    }

    /**
     * 情報を保存する
     * @param IDS
     * @return 
     */
    @PostMapping("/save")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> save(@RequestBody QualificationsEntityRequest req, @AuthenticationPrincipal OidcUser principal) {
        Integer id = qualificationsService.save(req, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok("保存しました。", id));
    }

    /**
     * 指定したIDのEntityを削除する
     * @param ID
     * @return 
     */
    @PostMapping("/delete/id")
    @ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> deleteById(@RequestBody IdRequest req, @AuthenticationPrincipal OidcUser principal) {
        Integer result = qualificationsService.deleteById(req.getId(), principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok("削除しました。", result));
    }

    /**
     * すべての資格情報を取得する
     * @return
     */
    @GetMapping("/get/")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<QualificationsEntity>>> getListForEmployee() {
        return ResponseEntity.ok(SimpleResponse.ok(qualificationsService.getListForEmployee()));
    }

    /**
     * すべての許認可情報を取得する
     * @return
     */
    @GetMapping("/get/license")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<QualificationsEntity>>> getListForCompany() {
        return ResponseEntity.ok(SimpleResponse.ok(qualificationsService.getListForCompany()));
    }

    /**
     * すべての資格情報を取得する
     * @return
     */
    @GetMapping("/get/all")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<QualificationsEntity>>> getListForEmployeeAllStatus() {
        return ResponseEntity.ok(SimpleResponse.ok(qualificationsService.getListForEmployeeAllStatus()));
    }

    /**
     * すべての許認可情報を取得する
     * @return
     */
    @GetMapping("/get/license/all")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<QualificationsEntity>>> getListForCompanyAllStatus() {
        return ResponseEntity.ok(SimpleResponse.ok(qualificationsService.getListForCompanyAllStatus()));
    }
}
