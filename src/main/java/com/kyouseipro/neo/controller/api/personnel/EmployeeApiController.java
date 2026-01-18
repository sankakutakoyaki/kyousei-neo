package com.kyouseipro.neo.controller.api.personnel;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.dto.ApiResponse;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.service.personnel.EmployeeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeApiController {
    private final EmployeeService employeeService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/employee/get/id")
	@ResponseBody
    // public Optional<EmployeeEntity> getById(@RequestParam int id) {
    //     return employeeService.getById(id);
    // }
    // public ResponseEntity<EmployeeEntity> getById(@RequestParam int id) {
    public ResponseEntity<EmployeeEntity> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(employeeService.getById(req.getId()).orElse(null));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/api/employee/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody EmployeeEntity entity, @AuthenticationPrincipal OidcUser principal) {
        int id = employeeService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * 情報を更新する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/api/employee/update/{type}")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> save(@RequestParam int id, @RequestParam String data, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody SimpleData data, @PathVariable String type, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        int resultId = 0;

        switch (type) {
            case "code":
                resultId = employeeService.updateCode(data.getNumber(), data.getText(), userName);
                break;
            case "phone":
                resultId = employeeService.updatePhone(data.getNumber(), data.getText(), userName);
                break;
            default:
                break;
        }

        return ResponseEntity.ok(ApiResponse.ok("保存しました。", resultId));
        // if (resultId > 0) {
        //     historyService.save(userName, "employees", "保存", 200, "成功");
        //     return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        // } else {
        //     historyService.save(userName, "employees", "保存", 400, "失敗");
        //     return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        // }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/employee/delete")
	@ResponseBody
    // public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     Integer id = employeeService.deleteByIds(ids, userName);
    //     if (id != null && id > 0) {
    //         historyService.save(userName, "employees", "削除", 200, "成功");
    //         return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    //     } else {
    //         historyService.save(userName, "employees", "削除", 400, "失敗");
    //         return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
    //     }
    // }
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        Integer id = employeeService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/employee/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        // historyService.save(userName, "employees", "ダウンロード", 0, "");
        return employeeService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}