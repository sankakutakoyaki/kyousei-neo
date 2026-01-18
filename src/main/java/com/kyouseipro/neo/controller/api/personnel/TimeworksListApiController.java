package com.kyouseipro.neo.controller.api.personnel;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.dto.ApiResponse;
import com.kyouseipro.neo.entity.dto.BetweenRequest;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksSummaryEntity;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.personnel.TimeworksListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TimeworksListApiController {
    private final TimeworksListService timeworksListService;
    private final EmployeeService employeeService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/api/timeworks/get/today")
	@ResponseBody
    public List<TimeworksListEntity> getTodaysList() {
        return timeworksListService.getTodaysList();
    }

    /**
     * Entityを取得する
     * @return
     */
    @PostMapping("/api/timeworks/get/today/id")
	@ResponseBody
    // public Optional<TimeworksListEntity> getTodaysByEmployeeId(@RequestParam int id) {
    //     Optional<TimeworksListEntity> opt = timeworksListService.getTodaysByEmployeeId(id);

    //     // 既に存在するならそのまま返す
    //     if (opt.isPresent()) {
    //         return opt;
    //     }

    //     // 存在しない場合は新規作成
    //     Optional<EmployeeEntity> empOpt = employeeService.getById(id);
    //     if (empOpt.isEmpty()) {
    //         return Optional.empty();
    //     }

    //     EmployeeEntity emp = empOpt.get();

    //     TimeworksListEntity entity = new TimeworksListEntity();
    //     entity.setEmployee_id(emp.getEmployee_id());
    //     entity.setFull_name(emp.getFull_name());
    //     entity.setWork_date(LocalDate.now());

    //     return Optional.of(entity);
    // }
    public ResponseEntity<TimeworksListEntity> getTodaysByEmployeeId(@RequestBody IdRequest req) {
        Optional<TimeworksListEntity> opt = timeworksListService.getTodaysByEmployeeId(req.getId());

        // 既に存在するならそのまま返す
        if (opt != null) {
            return ResponseEntity.ok(opt.orElse(null));
        }

        // 存在しない場合は新規作成
        Optional<EmployeeEntity> empOpt = employeeService.getById(req.getId());
        if (empOpt.isEmpty()) {
            return null;
        }

        EmployeeEntity emp = empOpt.get();

        TimeworksListEntity entity = new TimeworksListEntity();
        entity.setEmployee_id(emp.getEmployee_id());
        entity.setFull_name(emp.getFull_name());
        entity.setWork_date(LocalDate.now());

        return ResponseEntity.ok(Optional.of(entity).orElse(null));
    }

    /**
     * 指定した期間のEntityListを取得する
     * @return
     */
    @PostMapping("/api/timeworks/get/between/id")
	@ResponseBody
    // public List<TimeworksListEntity> getBetweenByEmployeeId(
    //             @RequestParam int id,
    //             @RequestParam LocalDate start,
    //             @RequestParam LocalDate end) {
    //     List<TimeworksListEntity> list = timeworksListService.getBetweenByEmployeeId(id, start, end);
    //     return list;
    // }
    public List<TimeworksListEntity> getBetweenByEmployeeId(@RequestBody BetweenRequest req) {
        return timeworksListService.getBetweenByEmployeeId(req.getId(), req.getStart(), req.getEnd());
    }

    /**
     * 指定した期間の確定済みも含めたEntityListを取得する
     * @return
     */
    @PostMapping("/api/timeworks/get/between/id/all")
	@ResponseBody
    // public List<TimeworksListEntity> getBetweenAllByEmployeeId(
    //             @RequestParam int id,
    //             @RequestParam LocalDate start,
    //             @RequestParam LocalDate end) {
    //     List<TimeworksListEntity> list = timeworksListService.getBetweenAllByEmployeeId(id, start, end);
    //     return list;
    // }
    public List<TimeworksListEntity> getBetweenAllByEmployeeId(@RequestBody BetweenRequest req) {
        return timeworksListService.getBetweenAllByEmployeeId(req.getId(), req.getStart(), req.getEnd());
    }

    /**
     * 指定した期間のEntityList概要版を取得する
     * @return
     */
    @PostMapping("/api/timeworks/summary/get/between/id")
	@ResponseBody
    // public List<TimeworksSummaryEntity> getBetweenSummary(
    //             @RequestParam int id,
    //             @RequestParam LocalDate start,
    //             @RequestParam LocalDate end) {
    //     if (id == 0) {
    //         return timeworksListService.getBetweenSummary(start, end);
    //     } else {
    //         return timeworksListService.getBetweenSummaryByOfficeId(id, start, end);
    //     }
    // }
    public List<TimeworksSummaryEntity> getBetweenSummary(@RequestBody BetweenRequest req) {
        if (req.getId() == 0) {
            return timeworksListService.getBetweenSummary(req.getStart(), req.getEnd());
        } else {
            return timeworksListService.getBetweenSummaryByOfficeId(req.getId(), req.getStart(), req.getEnd());
        }
    }

    /**
     * 勤怠情報を作成・更新する
     * @param timeworksEntity
     * @return
     */
    @PostMapping("/api/timeworks/regist/today")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> save(@RequestBody TimeworksListEntity timeworksEntity, @AuthenticationPrincipal OidcUser principal) {
    //     String editor = principal.getAttribute("preferred_username");
    //     Integer id = timeworksListService.save(timeworksEntity, editor);
    //     if (id != null && id > 0) {
    //         historyService.save(editor, "timeworks", "保存", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    //     } else if (id != null && id == -1) {
    //         historyService.save(editor, "timeworks", "保存", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("有給申請されているため\n勤務登録できません。", id));
    //     } else {
    //         historyService.save(editor, "timeworks", "保存", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody TimeworksListEntity timeworksEntity, @AuthenticationPrincipal OidcUser principal) {
        int id = timeworksListService.save(timeworksEntity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * 勤怠情報の確定を取り消す
     * @param id
     * @return
     */
    @PostMapping("/api/timeworks/confirm/reverse")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> reverseConrirm(@RequestParam int id, @AuthenticationPrincipal OidcUser principal) {
    //     String editor = principal.getAttribute("preferred_username");
    //     Integer newId = timeworksListService.reverseConfirm(id, editor);
    //     if (newId != null && newId > 0) {
    //         historyService.save(editor, "timeworks", "確定取消", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok("戻しました。", newId));
    //     } else {
    //         historyService.save(editor, "timeworks", "確定取消", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("戻せませんでした。"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> reverseConrirm(@RequestBody IdRequest req, @AuthenticationPrincipal OidcUser principal) {
        int newId = timeworksListService.reverseConfirm(req.getId(), principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("戻しました。", newId));
    }

    /**
     * 勤怠情報を作成・更新する
     * @param list
     * @return
     */
    @PostMapping("/api/timeworks/update/list")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> update(@RequestBody List<TimeworksListEntity> list, @AuthenticationPrincipal OidcUser principal) {
    //     String editor = principal.getAttribute("preferred_username");
    //     Integer id = timeworksListService.updateList(list, editor);
    //     if (id != null && id > 0) {
    //         historyService.save(editor, "timeworks", "保存", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    //     } else {
    //         historyService.save(editor, "timeworks", "保存", 200, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> update(@RequestBody List<TimeworksListEntity> list, @AuthenticationPrincipal OidcUser principal) {
        int id = timeworksListService.updateList(list, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/timeworks/download/csv")
	@ResponseBody
    // public String downloadCsvByIdsFromBetween(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {
    //     List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("ids");
    //     List<SimpleData> list = new ArrayList<>();
    //     for (Map<String, Object> item : items) {
    //         SimpleData data = new SimpleData();
    //         data.setNumber(((Number) item.get("number")).intValue());
    //         list.add(data);
    //     }
    //     String start = (String) body.get("start");
    //     String end = (String) body.get("end");
    //     String editor = principal.getAttribute("preferred_username");
    //     historyService.save(editor, "timeworks", "ダウンロード", 0, "");
    //     return timeworksListService.downloadCsvByIdsFromBetween(list, start, end, editor);
    // }
    public String downloadCsvByIdsFromBetween(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {
        IdListRequest ids = (IdListRequest) body.get("ids");
        BetweenRequest req = (BetweenRequest) body.get("req");
        return timeworksListService.downloadCsvByIdsFromBetween(ids, req.getStart(), req.getEnd(), principal.getAttribute("preferred_username"));
    }
}
