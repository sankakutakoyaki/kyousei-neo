package com.kyouseipro.neo.controller.api;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.sales.OrderListEntity;
import com.kyouseipro.neo.service.sales.OrderListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderListApiController {
    private final OrderListService orderListService;

    /**
     * OrderListを取得する
     * @return
     */
    @GetMapping("/order/get/list")
	@ResponseBody
    public List<OrderListEntity> getOrderList() {
        return orderListService.getOrderList();
    }
}
