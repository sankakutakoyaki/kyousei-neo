package com.kyouseipro.neo.controller.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.PaidHolidayEntity;
import com.kyouseipro.neo.entity.personnel.PaidHolidayListEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksSummaryEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.personnel.TimeworksListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TimeworksListApiController {
    private final TimeworksListService timeworksListService;
    private final EmployeeService employeeService;
    private final HistoryService historyService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/timeworks/get/today")
	@ResponseBody
    public List<TimeworksListEntity> getEntityList() {
        return timeworksListService.getTodaysList();
    }

    /**
     * Entityを取得する
     * @return
     */
    @PostMapping("/timeworks/get/today/id")
	@ResponseBody
    public TimeworksListEntity getTodaysEntityByEmployeeId(@RequestParam int id) {
        TimeworksListEntity entity = timeworksListService.getTodaysEntityByEmployeeId(id);
        if (entity == null) {
            entity = new TimeworksListEntity();
            EmployeeEntity emp = employeeService.getEmployeeById(id);
            if (emp != null) {
                entity.setEmployee_id(emp.getEmployee_id());
                entity.setFull_name(emp.getFull_name());
                entity.setWork_date(LocalDate.now());
            } else {
                return entity;
            }
        }
        return entity;
    }

    /**
     * 指定した期間のEntityListを取得する
     * @return
     */
    @PostMapping("/timeworks/get/between/id")
	@ResponseBody
    public List<TimeworksListEntity> getBetweenEntityByEmployeeId(
                @RequestParam int id,
                @RequestParam LocalDate start,
                @RequestParam LocalDate end) {
        List<TimeworksListEntity> list = timeworksListService.getBetweenEntityByEmployeeId(id, start, end);
        return list;
    }

    /**
     * 指定した期間の確定済みも含めたEntityListを取得する
     * @return
     */
    @PostMapping("/timeworks/get/between/id/all")
	@ResponseBody
    public List<TimeworksListEntity> getBetweenAllEntityByEmployeeId(
                @RequestParam int id,
                @RequestParam LocalDate start,
                @RequestParam LocalDate end) {
        List<TimeworksListEntity> list = timeworksListService.getBetweenAllEntityByEmployeeId(id, start, end);
        return list;
    }

    /**
     * 指定した期間のEntityList概要版を取得する
     * @return
     */
    @PostMapping("/timeworks/summary/get/between/id")
	@ResponseBody
    public List<TimeworksSummaryEntity> getBetweenSummaryEntityByEmployeeId(
                @RequestParam int id,
                @RequestParam LocalDate start,
                @RequestParam LocalDate end) {
        if (id == 0) {
            return timeworksListService.getBetweenSummaryEntity(start, end);
        } else {
            return timeworksListService.getBetweenSummaryEntityByOfficeId(id, start, end);
        }
        // List<TimeworksSummaryEntity> list = timeworksListService.getBetweenSummaryEntityByEmployeeId(id, start, end);
        // return list;
    }

    /**
     * 指定した年と営業所のEntityListを取得する
     * @return
     */
    @PostMapping("/timeworks/paidholiday/get/year")
	@ResponseBody
    public List<PaidHolidayListEntity> getPaidHolidayEntityFromYear (
                @RequestParam int id,
                @RequestParam String year) {
        return timeworksListService.getPaidHolidayEntityFromYear(id, year);
    }

    /**
     * 指定した年と従業員の有給リストを取得する
     * @return
     */
    @PostMapping("/timeworks/paidholiday/get/employeeid")
	@ResponseBody
    public List<PaidHolidayEntity> getPaidHolidayEntityFromEmployeeId (
                @RequestParam int id,
                @RequestParam String year) {
        return timeworksListService.getPaidHolidayEntityByEmployeeId(id, year);
    }

    /**
     * 指定した期間のEntityListを保存する
     * @return
     */
    @PostMapping("/timeworks/paidholiday/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> savePaidHolidayEntityByEmployeeId (
                @RequestBody PaidHolidayEntity entity,
                @AuthenticationPrincipal OidcUser principal) {
        String editor = principal.getAttribute("preferred_username");
        Integer id = timeworksListService.savePaidHolidayEntityByEmployeeId(entity, editor);
        if (id != null && id > 0) {
            historyService.saveHistory(editor, "paid_holiday", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.saveHistory(editor, "paid_holiday", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました\n期間が重複している可能性があります。"));
        }
    }

    /**
     * 指定したIDのEntityを削除する
     * @return
     */
    @PostMapping("/timeworks/paidholiday/delete/id")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deletePaidHolidayEntityById (
                @RequestParam int id,
                @AuthenticationPrincipal OidcUser principal) {
        String editor = principal.getAttribute("preferred_username");
        Integer result = timeworksListService.deletePaidHolidayEntityById(id, editor);
        if (result != null && result > 0) {
            historyService.saveHistory(editor, "paid_holiday", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("削除しました。", result));
        } else {
            historyService.saveHistory(editor, "paid_holiday", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * 勤怠情報を作成・更新する
     * @param timeworksEntity
     * @return
     */
    @PostMapping("/timeworks/regist/today")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveTimeworksToday(@RequestBody TimeworksListEntity timeworksEntity, @AuthenticationPrincipal OidcUser principal) {
        // LocalDate date = LocalDate.now();
        // String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS"));
        String editor = principal.getAttribute("preferred_username");
        // return timeworksListService.saveTodaysTimeworks(timeworksEntity, editor);
        Integer id = timeworksListService.saveTodaysTimeworks(timeworksEntity, editor);
        if (id != null && id > 0) {
            historyService.saveHistory(editor, "timeworks", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else if (id != null && id == -1) {
            historyService.saveHistory(editor, "timeworks", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("有給申請されているため\n勤務登録できません。", id));
        } else {
            historyService.saveHistory(editor, "timeworks", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * 勤怠情報の確定を取り消す
     * @param id
     * @return
     */
    @PostMapping("/timeworks/confirm/reverse")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> reverseConrirm(@RequestParam int id, @AuthenticationPrincipal OidcUser principal) {
        String editor = principal.getAttribute("preferred_username");
        Integer newId = timeworksListService.reverseConfirm(id, editor);
        if (newId != null && newId > 0) {
            historyService.saveHistory(editor, "timeworks", "確定取消", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("戻しました。", newId));
        } else {
            historyService.saveHistory(editor, "timeworks", "確定取消", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("戻せませんでした。"));
        }
    }

    /**
     * 勤怠情報を作成・更新する
     * @param list
     * @return
     */
    @PostMapping("/timeworks/update/list")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> updateTimeworksList(@RequestBody List<TimeworksListEntity> list, @AuthenticationPrincipal OidcUser principal) {
        String editor = principal.getAttribute("preferred_username");
        // return timeworksListService.updateTimeworksList(list, editor);
        Integer id = timeworksListService.updateTimeworksList(list, editor);
        if (id != null && id > 0) {
            historyService.saveHistory(editor, "timeworks", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.saveHistory(editor, "timeworks", "保存", 200, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/timeworks/download/csv")
	@ResponseBody
    public String downloadCsvEntityByBetweenDate(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("ids");
        List<SimpleData> list = new ArrayList<>();
        for (Map<String, Object> item : items) {
            SimpleData data = new SimpleData();
            data.setNumber(((Number) item.get("number")).intValue());
            list.add(data);
        }
        String start = (String) body.get("start");
        String end = (String) body.get("end");
        String editor = principal.getAttribute("preferred_username");
        historyService.saveHistory(editor, "timeworks", "ダウンロード", 0, "");
        return timeworksListService.downloadCsvByIds(list, start, end, editor);
    }
}
