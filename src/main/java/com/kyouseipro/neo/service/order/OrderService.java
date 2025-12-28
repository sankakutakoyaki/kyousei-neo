package com.kyouseipro.neo.service.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.order.OrderEntity;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.entity.order.WorkContentEntity;
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
    public OrderEntity getOrderById(String sql, int id) {
        // String sql = OrderSqlBuilder.buildFindByIdSql();
        OrderEntity orderEntity = orderRepository.findById(sql, id);
        List<OrderItemEntity> orderItemEntityList = orderItemRepository.findAllByOrderId(id, null);
        List<DeliveryStaffEntity> deliveryStaffEntityList = deliveryStaffRepository.findAllByOrderId(id, null);
        List<WorkContentEntity> workContentEntityList = workContentRepository.findAllByOrderId(id, null);
        orderEntity.setItem_list(orderItemEntityList);
        orderEntity.setStaff_list(deliveryStaffEntityList);
        orderEntity.setWork_list(workContentEntityList);
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
    // public Integer saveOrder(OrderEntity entity, 
    //                             List<OrderItemEntity> itemEntityList, 
    //                             List<DeliveryStaffEntity> staffEntityList, 
    //                             List<WorkContentEntity> workContentEntityList,
    //                             String editor) {
    //     String sql = "";
    //     int index = 1;
    //     if (entity.getOrder_id() > 0) {
    //         // 受注データが更新の場合
    //         sql = OrderSqlBuilder.buildUpdateOrderSql();
            
    //         // 商品リスト
    //         for (OrderItemEntity orderItemEntity : itemEntityList) {
    //             if (orderItemEntity.getState() == Enums.state.DELETE.getCode()) {
    //                 sql += OrderItemSqlBuilder.buildDeleteOrderItemSql(index++);
    //             } else {
    //                 if (orderItemEntity.getOrder_item_id() > 0) {
    //                     sql += OrderItemSqlBuilder.buildUpdateOrderItemSql(index++);
    //                 } else {
    //                     sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
    //                 }
    //             }
    //         }
    //         entity.setItem_list(itemEntityList);

    //         // 作業リスト
    //         for (WorkContentEntity workContentEntity : workContentEntityList) {
    //             if (workContentEntity.getState() == Enums.state.DELETE.getCode()) {
    //                 sql += WorkContentSqlBuilder.buildDeleteWorkContentSql(index++);
    //             } else {
    //                 if (workContentEntity.getWork_content_id() > 0) {
    //                     sql += WorkContentSqlBuilder.buildUpdateWorkContentSql(index++);
    //                 } else {
    //                     sql += WorkContentSqlBuilder.buildInsertWorkContentSql(index++);
    //                 }
    //             }
    //         }
    //         entity.setWork_list(workContentEntityList);

    //         // 配送員リスト
    //         for (DeliveryStaffEntity deliveryStaffEntity : staffEntityList) {
    //             if (deliveryStaffEntity.getState() == Enums.state.DELETE.getCode()) {
    //                 sql += DeliveryStaffSqlBuilder.buildDeleteDeliveryStaffSql(index++);
    //             } else {
    //                 if (deliveryStaffEntity.getDelivery_staff_id() > 0) {
    //                     sql += DeliveryStaffSqlBuilder.buildUpdateDeliveryStaffSql(index++);
    //                 } else {
    //                     sql += DeliveryStaffSqlBuilder.buildInsertDeliveryStaffSql(index++);
    //                 }
    //             }
    //         }
    //         entity.setStaff_list(staffEntityList);

    //         return orderRepository.updateOrder(sql, entity, editor);
    //     } else {
    //         // 受注データが新規の場合
    //         sql = OrderSqlBuilder.buildInsertOrderSql();

    //         // 商品リスト
    //         for(int i = 0; i < itemEntityList.size(); i++) {
    //             if (entity.getOrder_id() > 0) {
    //                 sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
    //             } else {
    //                 sql += OrderItemSqlBuilder.buildInsertOrderItemByNewOrderSql(index++);
    //             }
    //         }
    //         entity.setItem_list(itemEntityList);

    //         // 作業リスト
    //         for(int i = 0; i < workContentEntityList.size(); i++) {
    //             if (entity.getOrder_id() > 0) {
    //                 sql += WorkContentSqlBuilder.buildInsertWorkContentSql(index++);
    //             } else {
    //                 sql += WorkContentSqlBuilder.buildInsertWorkContentByNewOrderSql(index++);
    //             }
    //         }
    //         entity.setWork_list(workContentEntityList);

    //         // 配送員リスト
    //         for(int i = 0; i < staffEntityList.size(); i++) {
    //             if (entity.getOrder_id() > 0) {
    //                 sql += DeliveryStaffSqlBuilder.buildInsertDeliveryStaffSql(index++);
    //             } else {
    //                 sql += DeliveryStaffSqlBuilder.buildInsertDeliveryStaffByNewOrderSql(index++);
    //             }
    //         }
    //         entity.setStaff_list(staffEntityList);

    //         return orderRepository.insertOrder(sql, entity, editor);
    //     }
    // }    
    public Integer saveOrder(OrderEntity entity, 
                         List<OrderItemEntity> itemEntityList,
                         List<DeliveryStaffEntity> staffEntityList,
                         List<WorkContentEntity> workContentEntityList,
                         String editor) {

        return orderRepository.saveOrder(entity, itemEntityList, staffEntityList, workContentEntityList, editor);
    }

    /**
     * IDからOrderを削除
     * @param ids
     * @return
     */
    public Integer deleteOrderByIds(List<SimpleData> list, String userName) {
        // List<Integer> orderIds = Utilities.createSequenceByIds(list);
        // String sql = OrderSqlBuilder.buildDeleteOrderForIdsSql(orderIds.size());
        return orderRepository.deleteOrderByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvOrderByIds(List<SimpleData> list, String userName) {
        // List<Integer> orderIds = Utilities.createSequenceByIds(list);
        // String sql = OrderSqlBuilder.buildDownloadCsvOrderForIdsSql(orderIds.size());
        List<OrderEntity> orders = orderRepository.downloadCsvOrderByIds(list, userName);
        return CsvExporter.export(orders, OrderEntity.class);
    }
}
