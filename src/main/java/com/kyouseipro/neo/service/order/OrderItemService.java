package com.kyouseipro.neo.service.order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.interfaceis.HistoryTarget;
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
    public Optional<OrderItemEntity> getById(int id) {
        return orderItemRepository.findById(id);
    }

    public List<OrderItemEntity> getBetween(LocalDate start, LocalDate end) {
        return orderItemRepository.findByBetweenDate(start, end);
    }

    /**
     * 商品情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    @HistoryTarget(
        table = HistoryTables.ORDERITEMS,
        action = "保存"
    )
    public int save(List<OrderItemEntity> itemList, String editor) {
        return orderItemRepository.save(itemList, editor);
    }

    /**
     * IDからOrderItemを削除
     * @param ids
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.ORDERITEMS,
        action = "削除"
    )
    public int deleteByIds(List<SimpleData> list, String userName) {
        return orderItemRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list, String userName) {
        List<OrderItemEntity> orderItems = orderItemRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(orderItems, OrderItemEntity.class);
    }
}
