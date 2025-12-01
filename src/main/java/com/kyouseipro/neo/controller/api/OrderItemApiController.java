package com.kyouseipro.neo.controller.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.entity.sales.OrderListEntity;
import com.kyouseipro.neo.query.sql.sales.OrderItemSqlBuilder;
import com.kyouseipro.neo.service.sales.OrderItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderItemApiController {
    private final OrderItemService orderItemService;
    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/order/item/get/id")
	@ResponseBody
    public OrderItemEntity getEntityById(@RequestParam int id) {
        String sql = OrderItemSqlBuilder.buildFindByIdSql();
        return orderItemService.getOrderItemById(sql, id);
    }

    /**
     * 期間内の商品情報を取得する
     * @param start
     * @param end
     * @return
     */
    @PostMapping("/order/item/get/between")
	@ResponseBody
    public List<OrderItemEntity> getBetweenAllEntity(
                @RequestParam LocalDate start,
                @RequestParam LocalDate end) {
        List<OrderItemEntity> list = orderItemService.getBetweenOrderItemEntity(start, end);
        return list;
    }
}
