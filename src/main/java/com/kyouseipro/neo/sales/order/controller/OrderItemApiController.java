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
import com.kyouseipro.neo.dto.BetweenRequest;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.sales.order.entity.OrderItemEntity;
import com.kyouseipro.neo.sales.order.service.OrderItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderItemApiController {
    private final OrderItemService orderItemService;
    private final ObjectMapper objectMapper;
    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/item/get/id")
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
    @PostMapping("/item/get/between")
	@ResponseBody
    public List<OrderItemEntity> getBetween(@RequestBody BetweenRequest req) {
        List<OrderItemEntity> list = orderItemService.getBetween(req.getStart(), req.getEnd());
        return list;
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/item/save")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> save(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {
        List<OrderItemEntity> itemList = objectMapper.convertValue(body.get("list"), new TypeReference<List<OrderItemEntity>>() {});
        int id = orderItemService.save(itemList, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/item/delete")
	@ResponseBody
    public ResponseEntity<ApiResponse<Integer>> deleteByIds(@RequestBody IdListRequest list, @AuthenticationPrincipal OidcUser principal) {
        Integer id = orderItemService.deleteByIds(list, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(ApiResponse.ok(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/item/download/csv")
	@ResponseBody
    public String downloadCsvByIds(@RequestBody IdListRequest list, @AuthenticationPrincipal OidcUser principal) {
        return orderItemService.downloadCsvByIds(list, principal.getAttribute("preferred_username"));
    }
}
