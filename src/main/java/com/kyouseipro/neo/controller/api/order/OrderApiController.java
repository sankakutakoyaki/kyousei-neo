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
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.order.OrderEntity;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.entity.order.WorkContentEntity;
import com.kyouseipro.neo.service.order.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/api/order/get/id")
	@ResponseBody
    public Optional<OrderEntity> getById(@RequestBody IdRequest req) {
        // String sql = OrderSqlBuilder.buildFindById();
        return orderService.getById(req.getId());
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/api/order/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");

        OrderEntity orderEntity = objectMapper.convertValue(body.get("orderEntity"), OrderEntity.class);
        List<OrderItemEntity> itemList = objectMapper.convertValue(body.get("itemEntityList"), new TypeReference<List<OrderItemEntity>>() {});
        List<DeliveryStaffEntity> staffList = objectMapper.convertValue(body.get("staffEntityList"), new TypeReference<List<DeliveryStaffEntity>>() {});
        List<WorkContentEntity> workList = objectMapper.convertValue(body.get("workEntityList"), new TypeReference<List<WorkContentEntity>>() {});

        int id = orderService.save(orderEntity, itemList, staffList, workList, principal.getAttribute("preferred_username"));
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
    @PostMapping("/api/order/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        int id = orderService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        // if (id != null && id > 0) {
        //     historyService.save(userName, "orders", "削除", 200, "成功");
        //     return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        // } else {
        //     historyService.save(userName, "orders", "削除", 400, "失敗");
        //     return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        // }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/api/order/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        // String userName = principal.getAttribute("preferred_username");
        // historyService.save(userName, "orders", "ダウンロード", 0, "");
        return orderService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}