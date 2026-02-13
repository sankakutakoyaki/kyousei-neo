package com.kyouseipro.neo.sales.order.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
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
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.dto.BetweenRequest;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.sales.order.entity.OrderItemEntity;
import com.kyouseipro.neo.sales.order.service.OrderItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/order/item")
public class OrderItemApiController {
    private final OrderItemService orderItemService;
    private final ObjectMapper objectMapper;
    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/get/id")
	@ResponseBody
    public ResponseEntity<SimpleResponse<OrderItemEntity>> getById(@RequestBody IdRequest req) {
        return ResponseEntity.ok(new SimpleResponse<>(null, orderItemService.getById(req.getId())));
    }

    /**
     * 期間内の商品情報を取得する
     * @param start
     * @param end
     * @return
     */
    @PostMapping("/get/between")
	@ResponseBody
    public SimpleResponse<List<OrderItemEntity>> getBetween(@RequestBody BetweenRequest req) {
        return new SimpleResponse<>(null, orderItemService.getBetween(req.getStart(), req.getEnd()));
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/save")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> save(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OidcUser principal) {
        List<OrderItemEntity> itemList = objectMapper.convertValue(body.get("list"), new TypeReference<List<OrderItemEntity>>() {});
        int id = orderItemService.save(itemList, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(new SimpleResponse<>("保存しました。", id));
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/delete")
	@ResponseBody
    public ResponseEntity<SimpleResponse<Integer>> deleteByIds(@RequestBody IdListRequest list, @AuthenticationPrincipal OidcUser principal) {
        Integer id = orderItemService.deleteByIds(list, principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(new SimpleResponse<>(id + "件削除しました。", id));
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    // @PostMapping("/item/download/csv")
	// @ResponseBody
    // public String downloadCsvByIds(@RequestBody IdListRequest list, @AuthenticationPrincipal OidcUser principal) {
    //     return orderItemService.downloadCsvByIds(list, principal.getAttribute("preferred_username"));
    // }
    @PostMapping(value = "/download/csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadCsvByIds(@RequestBody IdListRequest ids, @AuthenticationPrincipal OidcUser principal) {
        String csv = orderItemService.downloadCsvByIds(ids, principal.getAttribute("preferred_username"));
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv";

        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }
}
