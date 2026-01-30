package com.kyouseipro.neo.sales.order.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.dto.BetweenRequest;
import com.kyouseipro.neo.sales.order.entity.OrderListEntity;
import com.kyouseipro.neo.sales.order.service.OrderListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderListApiController {
    private final OrderListService orderListService;

    /**
     * OrderListを取得する
     * @return
     */
    @GetMapping("/get/list")
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
    @PostMapping("/get/between")
	@ResponseBody
    public List<OrderListEntity> getBetween(@RequestBody BetweenRequest req) {
        List<OrderListEntity> list = orderListService.getBetween(req.getStart(), req.getEnd());
        return list;
    }
}
