package com.kyouseipro.neo.controller.api.order;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.dto.BetweenRequest;
import com.kyouseipro.neo.entity.order.OrderListEntity;
import com.kyouseipro.neo.service.order.OrderListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderListApiController {
    private final OrderListService orderListService;

    /**
     * OrderListを取得する
     * @return
     */
    @GetMapping("/api/order/get/list")
	@ResponseBody
    public List<OrderListEntity> getList() {
        return orderListService.getList();
    }


    /**
     * 期間内の受注情報を取得する
     * @param start
     * @param end
     * @return
     */
    @PostMapping("/api/order/get/between")
	@ResponseBody
    // public List<OrderListEntity> getBetween(
    //             @RequestParam LocalDate start,
    //             @RequestParam LocalDate end) {
    public List<OrderListEntity> getBetween(@RequestBody BetweenRequest req) {
        List<OrderListEntity> list = orderListService.getBetween(req.getStart(), req.getEnd());
        return list;
    }
}
