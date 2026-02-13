package com.kyouseipro.neo.sales.order.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.dto.CsvExporter;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.interfaces.HistoryTarget;
import com.kyouseipro.neo.sales.order.entity.DeliveryStaffEntity;
import com.kyouseipro.neo.sales.order.entity.OrderEntity;
import com.kyouseipro.neo.sales.order.entity.OrderItemEntity;
import com.kyouseipro.neo.sales.order.entity.WorkContentEntity;
import com.kyouseipro.neo.sales.order.repository.DeliveryStaffRepository;
import com.kyouseipro.neo.sales.order.repository.OrderItemRepository;
import com.kyouseipro.neo.sales.order.repository.OrderRepository;
import com.kyouseipro.neo.sales.order.repository.WorkContentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryStaffRepository deliveryStaffRepository;
    private final WorkContentRepository workContentRepository;

    /**
     * 指定されたIDの受注情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 受注ID
     * @return OrderEntity または null
     */
    public OrderEntity getById(int id) {
        OrderEntity opt = orderRepository.findById(id);
        if (opt == null) return null;

        OrderEntity order = opt;

        order.setItemList(
            orderItemRepository.findAllByOrderId(id, null)
        );
        order.setStaffList(
            deliveryStaffRepository.findAllByOrderId(id, null)
        );
        order.setWorkList(
            workContentRepository.findAllByOrderId(id, null)
        );

        return order;
    }

    /**
     * 保存
     * @param entity
     * @param itemEntityList
     * @param staffEntityList
     * @param workContentEntityList
     * @param editor
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.ORDERS,
        action = "保存"
    )
    public int save(OrderEntity entity, 
                         List<OrderItemEntity> itemEntityList,
                         List<DeliveryStaffEntity> staffEntityList,
                         List<WorkContentEntity> workContentEntityList,
                         String editor) {

        return orderRepository.save(entity, itemEntityList, staffEntityList, workContentEntityList, editor);
    }

    /**
     * IDからOrderを削除
     * @param ids
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.ORDERS,
        action = "削除"
    )
    public int deleteByIds(IdListRequest list, String userName) {
        return orderRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<OrderEntity> orders = orderRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(orders, OrderEntity.class);
    }
}
