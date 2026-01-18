package com.kyouseipro.neo.controller.api.order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.entity.dto.ApiResponse;
import com.kyouseipro.neo.entity.dto.BetweenRequest;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.service.order.OrderItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderItemApiController {
    private final OrderItemService orderItemService;
    private final ObjectMapper objectMapper;
    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/order/item/get/id")
	@ResponseBody
    public Optional<OrderItemEntity> getById(@RequestBody IdRequest req) {
        return orderItemService.getById(req.getId());
    }

    /**
     * 期間内の商品情報を取得する
     * @param start
     * @param end
     * @return
     */
    @PostMapping("/api/order/item/get/between")
	@ResponseBody
    // public List<OrderItemEntity> getBetween(
    //             @RequestParam LocalDate start,
    //             @RequestParam LocalDate end) {
    public List<OrderItemEntity> getBetween(@RequestBody BetweenRequest req) {
        List<OrderItemEntity> list = orderItemService.getBetween(req.getStart(), req.getEnd());
        return list;
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/api/order/item/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        List<OrderItemEntity> itemList = objectMapper.convertValue(body.get("list"), new TypeReference<List<OrderItemEntity>>() {});
        int id = orderItemService.save(itemList, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        // if (id != null && id > 0) {
        //     historyService.save(userName, "orders", "保存", 200, "成功");
        //     return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        // } else {
        //     historyService.save(userName, "orders", "保存", 400, "失敗");
        //     return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        // }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/order/item/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest list, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        Integer id = orderItemService.deleteByIds(list, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        // if (id != null && id > 0) {
        //     historyService.save(userName, "order_items", "削除", 200, "成功");
        //     return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        // } else {
        //     historyService.save(userName, "order_items", "削除", 400, "失敗");
        //     return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        // }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/order/item/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest list, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        // historyService.save(userName, "orders_item", "ダウンロード", 0, "");
        return orderItemService.downloadCsvByIds(list, principal.getAttribute("preferred_username"));
    }
}
