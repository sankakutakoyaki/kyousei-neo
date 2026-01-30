package com.kyouseipro.neo.sales.order.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.sales.order.entity.OrderListEntity;
import com.kyouseipro.neo.sales.order.repository.OrderListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderListService {
    private final OrderListRepository orderListRepository;

    /**
     * すべてのOrderを取得
     * @return
     */
    public List<OrderListEntity> getList() {
        return orderListRepository.findAll();
    }

    /**
     * 
     * @param start
     * @param end
     * @return
     */
    public List<OrderListEntity> getBetween(LocalDate start, LocalDate end) {
        return orderListRepository.findByBetween(start, end);
    }
}
