package com.kyouseipro.neo.service.order;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.repository.order.OrderItemRepository;

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
    public OrderItemEntity getOrderItemById(int id) {
        // String sql = OrderSqlBuilder.buildFindByIdSql();
        return orderItemRepository.findById(id);
    }

    public List<OrderItemEntity> getBetweenOrderItemEntity(LocalDate start, LocalDate end) {
        return orderItemRepository.findByEntityFromBetweenDate(start, end);
    }

    /**
     * 商品情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer saveOrderItem(List<OrderItemEntity> itemList, String editor) {
        // String sql = "";
        // int index = 1;

        // for (OrderItemEntity entity : itemList) {
        //     if (entity.getState() == Enums.state.DELETE.getCode()) {
        //         sql += OrderItemSqlBuilder.buildDeleteOrderItemSql(index++);
        //     } else {
        //         if (entity.getOrder_item_id() > 0) {
        //             sql += OrderItemSqlBuilder.buildUpdateOrderItemSql(index++);
        //         } else {
        //             sql += OrderItemSqlBuilder.buildInsertOrderItemSql(index++);
        //         }
        //     }
        // }
        return orderItemRepository.saveOrderItemList(itemList, editor);
    }

    /**
     * IDからOrderItemを削除
     * @param ids
     * @return
     */
    public Integer deleteOrderItemByIds(List<SimpleData> list, String userName) {
        // List<Integer> orderItemIds = Utilities.createSequenceByIds(list);
        // String sql = OrderItemSqlBuilder.buildDeleteOrderItemForIdsSql(orderItemIds.size());
        return orderItemRepository.deleteOrderItemByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvOrderByIds(List<SimpleData> list, String userName) {
        // List<Integer> orderIds = Utilities.createSequenceByIds(list);
        // String sql = OrderItemSqlBuilder.buildDownloadCsvOrderItemForIdsSql(orderIds.size());
        List<OrderItemEntity> orderItems = orderItemRepository.downloadCsvOrderItemByIds(list, userName);
        return CsvExporter.export(orderItems, OrderItemEntity.class);
    }
}
