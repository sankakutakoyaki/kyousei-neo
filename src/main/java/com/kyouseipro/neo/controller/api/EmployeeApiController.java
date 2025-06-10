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

import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.data.SimpleData;
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
    @PostMapping("/employee/get/id")
	@ResponseBody
    public EmployeeEntity getEntityById(@RequestParam int id) {
        return employeeService.getEmployeeById(id);
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/employee/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveEmployee(@RequestBody EmployeeEntity entity, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = employeeService.saveEmployee(entity, userName);
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
    @PostMapping("/employee/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = employeeService.deleteEmployeeByIds(ids, userName);
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
    @PostMapping("/employee/download/csv")
	@ResponseBody
    public String downloadCsvEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        return employeeService.downloadCsvEmployeeByIds(ids, userName);
    }

    // /**
    //  * すべてのコンボボックス用取引先情報を取得する
    //  * @return
    //  */
    // @GetMapping("/employee/get/combo")
	// @ResponseBody
    // public List<EmployeeEntity> getEmployeeCombo() {
    //     return comboBoxService.getClient();
    // }
}