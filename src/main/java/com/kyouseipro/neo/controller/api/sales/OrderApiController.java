package com.kyouseipro.neo.controller.api.sales;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.entity.data.ApiResponse;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.sales.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.sales.OrderEntity;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.entity.sales.WorkContentEntity;
import com.kyouseipro.neo.query.sql.sales.OrderSqlBuilder;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.sales.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderService orderService;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/order/get/id")
	@ResponseBody
    public OrderEntity getEntityById(@RequestParam int id) {
        String sql = OrderSqlBuilder.buildFindByIdSql();
        return orderService.getOrderById(sql, id);
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/order/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> saveOrder(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");

        OrderEntity orderEntity = objectMapper.convertValue(body.get("orderEntity"), OrderEntity.class);
        List<OrderItemEntity> itemList = objectMapper.convertValue(body.get("itemEntityList"), new TypeReference<List<OrderItemEntity>>() {});
        List<DeliveryStaffEntity> staffList = objectMapper.convertValue(body.get("staffEntityList"), new TypeReference<List<DeliveryStaffEntity>>() {});
        List<WorkContentEntity> workList = objectMapper.convertValue(body.get("workEntityList"), new TypeReference<List<WorkContentEntity>>() {});

        Integer id = orderService.saveOrder(orderEntity, itemList, staffList, workList, userName);
        if (id != null && id > 0) {
            historyService.saveHistory(userName, "orders", "保存", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
        } else {
            historyService.saveHistory(userName, "orders", "保存", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("保存に失敗しました"));
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/order/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        Integer id = orderService.deleteOrderByIds(ids, userName);
        if (id != null && id > 0) {
            historyService.saveHistory(userName, "orders", "削除", 200, "成功");
            return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
        } else {
            historyService.saveHistory(userName, "orders", "削除", 400, "失敗");
            return ResponseEntity.badRequest().body(ApiResponse.error("削除に失敗しました"));
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/order/download/csv")
	@ResponseBody
    public String downloadCsvEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        historyService.saveHistory(userName, "orders", "ダウンロード", 0, "");
        return orderService.downloadCsvOrderByIds(ids, userName);
    }
}