package com.kyouseipro.neo.controller.api.personnel;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.personnel.PaidHolidayEntity;
import com.kyouseipro.neo.entity.personnel.PaidHolidayListEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.PaidHolidayService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PaidHolidayApiController {
    private final PaidHolidayService paidHolidayService;
    private final HistoryService historyService;

    /**
     * 指定した年と営業所のEntityListを取得する
     * @return
     */
    @PostMapping("/api/timeworks/paidholiday/get/year")
	@ResponseBody
    public List<PaidHolidayListEntity> getByOfficeIdFromYear (
                @RequestParam int id,
                @RequestParam String year) {
        return paidHolidayService.getByOfficeIdFromYear(id, year);
    }

    /**
     * 指定した年と従業員の有給リストを取得する
     * @return
     */
    @PostMapping("/api/timeworks/paidholiday/get/employeeid")
	@ResponseBody
    public List<PaidHolidayEntity> getByEmployeeIdFromYear (
                @RequestParam int id,
                @RequestParam String year) {
        return paidHolidayService.getByEmployeeIdFromYear(id, year);
    }

    /**
     * 指定した期間のEntityListを保存する
     * @return
     */
    @PostMapping("/api/timeworks/paidholiday/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save (
                @RequestBody PaidHolidayEntity entity,
                @AuthenticationPrincipal OidcUser principal) {
        String editor = principal.getAttribute("preferred_username");
        Integer id = paidHolidayService.insert(entity, editor);
        if (id != null && id > 0) {
            historyService.save(editor, "paid_holiday", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.save(editor, "paid_holiday", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました\n期間が重複している可能性があります。"));
        }
    }

    /**
     * 指定したIDのEntityを削除する
     * @return
     */
    @PostMapping("/api/timeworks/paidholiday/delete/id")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteById (
                @RequestParam int id,
                @AuthenticationPrincipal OidcUser principal) {
        String editor = principal.getAttribute("preferred_username");
        Integer result = paidHolidayService.deleteById(id, editor);
        if (result != null && result > 0) {
            historyService.save(editor, "paid_holiday", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("削除しました。", result));
        } else {
            historyService.save(editor, "paid_holiday", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }
}
