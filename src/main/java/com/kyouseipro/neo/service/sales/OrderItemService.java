package com.kyouseipro.neo.service.sales;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.repository.sales.OrderItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    /**
     * 指定されたIDの受注情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 受注ID
     * @return OrderEntity または null
     */
    public OrderItemEntity getOrderItemById(String sql, int id) {
        // String sql = OrderSqlBuilder.buildFindByIdSql();
        return orderItemRepository.findById(sql, id);
    }

    public List<OrderItemEntity> getBetweenOrderItemEntity(LocalDate start, LocalDate end) {
        return orderItemRepository.findByEntityFromBetweenDate(start, end);
    }
}
