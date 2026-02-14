package com.kyouseipro.neo.personnel.employee.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntityRequest;
import com.kyouseipro.neo.personnel.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeApiController {
    private final EmployeeService employeeService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/id")
	@ResponseBody
    public ResponseEntity<SimpleResponse<EmployeeEntity>> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(employeeService.getById(req.getId())));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/save")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> save(@RequestBody EmployeeEntityRequest entity, @AuthenticationPrincipal OidcUser principal) {
        int id = employeeService.save(entity, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok("保存しました。", id));
    }

    /**
     * 情報を更新する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/update/{type}")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> save(@RequestBody SimpleData data, @PathVariable String type, @AuthenticationPrincipal OidcUser principal) {
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

        return ResponseEntity.ok(SimpleResponse.ok("保存しました。", resultId));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/delete")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        Integer id = employeeService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(SimpleResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping(value = "/download/csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        String csv = employeeService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv";

        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }
}