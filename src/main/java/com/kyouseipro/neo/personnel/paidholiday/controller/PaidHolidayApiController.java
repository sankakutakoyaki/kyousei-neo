package com.kyouseipro.neo.personnel.paidholiday.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.personnel.paidholiday.entity.PaidHolidayEntity;
import com.kyouseipro.neo.personnel.paidholiday.entity.PaidHolidayListEntity;
import com.kyouseipro.neo.personnel.paidholiday.service.PaidHolidayService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/timeworks")
public class PaidHolidayApiController {
    private final PaidHolidayService paidHolidayService;

    /**
     * 指定した年と営業所のEntityListを取得する
     * @return
     */
    @PostMapping("/paidholiday/get/year")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<PaidHolidayListEntity>>> getByOfficeIdFromYear (@RequestBody SimpleData data) {
        return ResponseEntity.ok(SimpleResponse.ok(paidHolidayService.getByOfficeIdFromYear(data.getNumber(), data.getText())));
    }

    /**
     * 指定した年と従業員の有給リストを取得する
     * @return
     */
    @PostMapping("/paidholiday/get/employeeid")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<PaidHolidayEntity>>> getByEmployeeIdFromYear (@RequestBody SimpleData data) {
        return ResponseEntity.ok(SimpleResponse.ok(paidHolidayService.getByEmployeeIdFromYear(data.getNumber(), data.getText())));
    }

    /**
     * 指定した期間のEntityListを保存する
     * @return
     */
    @PostMapping("/paidholiday/save")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> save (@RequestBody PaidHolidayEntity entity, @AuthenticationPrincipal OidcUser principal) {
        int id = paidHolidayService.insert(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok("保存しました。", id));              
    }

    /**
     * 指定したIDのEntityを削除する
     * @return
     */
    @PostMapping("/paidholiday/delete/id")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> deleteById (@RequestBody IdRequest req, @AuthenticationPrincipal OidcUser principal) {
        int result = paidHolidayService.deleteById(req.getId(), principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok("削除しました。", result));
    }
}
