package com.kyouseipro.neo.controller.api.regist;

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
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.regist.WorkPriceEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.regist.WorkPriceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkPriceApiController {
    private final WorkPriceService workPriceService;
    private final HistoryService historyService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/work/price/get/id")
	@ResponseBody
    public WorkPriceEntity getEntityById(@RequestParam int id) {
        return workPriceService.getWorkPriceById(id);
    }


    /**
     * Listを取得する
     * @return
     */
    @GetMapping("/work/price/get/list")
	@ResponseBody
    public List<WorkPriceEntity> getWorkItemList() {
        return workPriceService.getList();
    }

    /**
     * 
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/work/price/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveWorkPrice(@RequestBody WorkPriceEntity entity, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = workPriceService.saveWorkPrice(entity, userName);
        if (id != null && id > 0) {
            historyService.saveHistory(userName, "employees", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.saveHistory(userName, "employees", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/work/price/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteWorkPriceByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = workPriceService.deleteWorkPriceByIds(ids, userName);
        if (id != null && id > 0) {
            historyService.saveHistory(userName, "order_items", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        } else {
            historyService.saveHistory(userName, "order_items", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }
}
