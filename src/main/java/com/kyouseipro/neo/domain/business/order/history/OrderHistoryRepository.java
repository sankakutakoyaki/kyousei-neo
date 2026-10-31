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

    public enum DispatchEvent {
        DISPATCH_CREATED, DISPATCH_MOVED, DISPATCH_REMOVED, DISPATCH_RESET, DISPATCH_REORDERED
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void recordDispatch(long assignmentId, DispatchEvent event, String editor) {
        repository.updateRequired("""
            INSERT INTO dbo.order_histories
                (order_id, order_version, event_type, editor, dispatch_assignment_id, dispatch_version)
            SELECT da.order_id, o.version, ?, ?, da.dispatch_assignment_id, da.version
            FROM dbo.dispatch_assignments da
            JOIN dbo.orders o ON o.order_id = da.order_id
            WHERE da.dispatch_assignment_id = ?
            """, List.of(event.name(), editor, assignmentId), "配車履歴を記録できませんでした。");
    }
}
