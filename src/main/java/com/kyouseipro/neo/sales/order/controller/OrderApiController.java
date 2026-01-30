package com.kyouseipro.neo.sales.order.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.dto.ApiResponse;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.sales.order.entity.DeliveryStaffEntity;
import com.kyouseipro.neo.sales.order.entity.OrderEntity;
import com.kyouseipro.neo.sales.order.entity.OrderItemEntity;
import com.kyouseipro.neo.sales.order.entity.WorkContentEntity;
import com.kyouseipro.neo.sales.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderApiController {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/id")
	@ResponseBody
    public Optional<OrderEntity> getById(@RequestBody IdRequest req) {
        return orderService.getById(req.getId());
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {

        OrderEntity orderEntity = objectMapper.convertValue(body.get("orderEntity"), OrderEntity.class);
        List<OrderItemEntity> itemList = objectMapper.convertValue(body.get("itemEntityList"), new TypeReference<List<OrderItemEntity>>() {});
        List<DeliveryStaffEntity> staffList = objectMapper.convertValue(body.get("staffEntityList"), new TypeReference<List<DeliveryStaffEntity>>() {});
        List<WorkContentEntity> workList = objectMapper.convertValue(body.get("workEntityList"), new TypeReference<List<WorkContentEntity>>() {});

        int id = orderService.save(orderEntity, itemList, staffList, workList, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        int id = orderService.deleteByIds(ids, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        return orderService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
    }
}