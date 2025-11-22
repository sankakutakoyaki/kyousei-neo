package com.kyouseipro.neo.service.sales;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.sales.OrderEntity;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.repository.sales.OrderItemRepository;
import com.kyouseipro.neo.repository.sales.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 指定されたIDの受注情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 受注ID
     * @return OrderEntity または null
     */
    public OrderEntity getOrderById(int id) {
        OrderEntity orderEntity = orderRepository.findById(id);
        List<OrderItemEntity> orderItemEntityList = orderItemRepository.findAllByOrderId(id, null);
        orderEntity.setItem_list(orderItemEntityList);
        return orderEntity;
    }

    /**
     * 受注情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer saveOrder(OrderEntity entity, List<OrderItemEntity> itemEntityList, String editor) {
        if (entity.getOrder_id() > 0) {
            return orderRepository.updateOrder(entity, itemEntityList, editor);
        } else {
            return orderRepository.insertOrder(entity, itemEntityList, editor);
        }
    }

    /**
     * IDからOrderを削除
     * @param ids
     * @return
     */
    public Integer deleteOrderByIds(List<SimpleData> list, String userName) {
        return orderRepository.deleteOrderByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvOrderByIds(List<SimpleData> list, String userName) {
        List<OrderEntity> orders = orderRepository.downloadCsvOrderByIds(list, userName);
        return CsvExporter.export(orders, OrderEntity.class);
    }
}
