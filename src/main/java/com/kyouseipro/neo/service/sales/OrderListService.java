package com.kyouseipro.neo.service.sales;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.sales.OrderListEntity;
import com.kyouseipro.neo.repository.sales.OrderListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderListService {
    private final OrderListRepository orderListRepository;

    /**
     * すべてのOrderを取得
     * @return
     */
    public List<OrderListEntity> getOrderList() {
        return orderListRepository.findAll();
    }

    /**
     * 
     * @param start
     * @param end
     * @return
     */
    public List<OrderListEntity> getBetweenOrderEntity(LocalDate start, LocalDate end) {
        return orderListRepository.findByEntityFromBetweenDate(start, end);
    }
}
