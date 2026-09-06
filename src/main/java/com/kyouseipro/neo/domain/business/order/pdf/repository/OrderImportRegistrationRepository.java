package com.kyouseipro.neo.domain.business.order.pdf.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.kyouseipro.neo.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;

/** All operations participate in the registration transaction. */
@Repository
@RequiredArgsConstructor
public class OrderImportRegistrationRepository {
    private final JdbcTemplate jdbc;
    public record ImportLock(long shipperId, Long orderId) {}

    public ImportLock lock(long importId) {
        var rows = jdbc.query("""
            SELECT prime_constractor_id, order_id FROM order_imports WITH (UPDLOCK, HOLDLOCK)
            WHERE order_import_id = ? AND state = 0
            """, (rs, n) -> new ImportLock(rs.getLong(1), rs.getObject(2) == null ? null : rs.getLong(2)), importId);
        if (rows.isEmpty()) throw new BusinessException("取込PDFが見つかりません。");
        return rows.get(0);
    }
    public long reviewId(long importId) {
        var ids = jdbc.query("""
            SELECT TOP 1 document_ai_review_id FROM ai_document_reviews WITH (UPDLOCK, HOLDLOCK)
            WHERE source_type = 'ORDER_IMPORT' AND source_id = ? AND state = 0
            ORDER BY document_ai_review_id DESC
            """, (rs, n) -> rs.getLong(1), importId);
        if (ids.isEmpty()) throw new BusinessException("読取結果がありません。PDFを読み取ってから確認保存してください。");
        return ids.get(0);
    }
    public void finish(long importId, long orderId, long reviewId, String confirmed, String editor) {
        int reviews = jdbc.update("""
            UPDATE ai_document_reviews SET confirmed_result = ?, review_status = 'CONFIRMED',
                reviewed_by = ?, reviewed_date = SYSDATETIME(), update_date = SYSDATETIME(), version = version + 1
            WHERE document_ai_review_id = ? AND source_type = 'ORDER_IMPORT' AND source_id = ? AND state = 0
            """, confirmed, editor, reviewId, importId);
        int imports = jdbc.update("""
            UPDATE order_imports SET order_id = ?, ocr_result = ?, ocr_status = 'COMPLETE',
                update_date = SYSDATETIME(), version = version + 1
            WHERE order_import_id = ? AND order_id IS NULL AND state = 0
            """, orderId, confirmed, importId);
        if (reviews != 1 || imports != 1) throw new BusinessException("取込状態が変わりました。再読み込みしてください。");
    }
}
