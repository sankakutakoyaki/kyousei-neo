package com.kyouseipro.neo.controller.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.sales.OrderEntity;
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


    /**
     * 期間内の受注情報を取得する
     * @param start
     * @param end
     * @return
     */
    @PostMapping("/order/get/between")
	@ResponseBody
    public List<OrderListEntity> getBetweenAllEntity(
                @RequestParam LocalDate start,
                @RequestParam LocalDate end) {
        List<OrderListEntity> list = orderListService.getBetweenOrderEntity(start, end);
        return list;
    }
}
