package com.kyouseipro.neo.service.order;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.controller.dto.CsvExporter;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.order.OrderEntity;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.entity.order.WorkContentEntity;
import com.kyouseipro.neo.interfaceis.HistoryTarget;
import com.kyouseipro.neo.repository.order.DeliveryStaffRepository;
import com.kyouseipro.neo.repository.order.OrderItemRepository;
import com.kyouseipro.neo.repository.order.OrderRepository;
import com.kyouseipro.neo.repository.order.WorkContentRepository;

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
    public Optional<OrderEntity> getById(int id) {
        Optional<OrderEntity> opt = orderRepository.findById(id);

        if (opt.isEmpty()) {
            return Optional.empty();
        }

        OrderEntity order = opt.get();

        order.setItemList(
            orderItemRepository.findAllByOrderId(id, null)
        );
        order.setStaffList(
            deliveryStaffRepository.findAllByOrderId(id, null)
        );
        order.setWorkList(
            workContentRepository.findAllByOrderId(id, null)
        );

        return Optional.of(order);
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
