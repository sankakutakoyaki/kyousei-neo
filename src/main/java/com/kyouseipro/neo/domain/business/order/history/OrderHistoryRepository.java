package com.kyouseipro.neo.domain.business.order.history;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderHistoryRepository {
    private final SqlRepository repository;

    // 受注・明細・既存ログと同じトランザクションで記録する。
    @Transactional(propagation = Propagation.MANDATORY)
    public void recordSaved(Long orderId, boolean created, String editor) {
        repository.updateRequired("""
            INSERT INTO dbo.order_histories (order_id, order_version, event_type, editor)
            SELECT order_id, version, ?, ? FROM dbo.orders WHERE order_id = ?
            """, List.of(created ? "ORDER_CREATED" : "ORDER_SAVED", editor, orderId),
            "受注履歴を記録できませんでした。");
    }
}
