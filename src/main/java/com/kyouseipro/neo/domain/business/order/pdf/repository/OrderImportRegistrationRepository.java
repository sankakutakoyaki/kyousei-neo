package com.kyouseipro.neo.domain.business.order.pdf.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

/** All operations participate in the registration transaction. */
@Repository
@RequiredArgsConstructor
public class OrderImportRegistrationRepository {
    private final SqlRepository sqlRepository;
    private final BaseSqlRepository baseRepository; 
    public record ImportLock(long shipperId, Long orderId, int version) {}
    public record ReviewLock(long reviewId, int version) {}

    public ImportLock lock(long importId) {
        var rows = sqlRepository.queryList(
            """
            SELECT prime_constractor_id, order_id, version FROM order_imports WITH (UPDLOCK, HOLDLOCK)
            WHERE order_import_id = ? AND state = ?
            """,
            (ps, ignored) -> {
                ps.setLong(1, importId);
                ps.setInt(2, State.INITIAL.getCode());
            },
            rs -> new ImportLock(
                rs.getLong("prime_constractor_id"),
                rs.getObject("order_id") == null ? null : rs.getLong("order_id"),
                rs.getInt("version")
            ), null
        );
        if (rows.isEmpty()) { throw new BusinessException("取込PDFが見つかりません。"); }
        return rows.get(0);
    }

    public ReviewLock lockReview(long importId) {
        var rows = sqlRepository.queryList(
                """
                SELECT TOP 1 document_ai_review_id, version FROM ai_document_reviews WITH (UPDLOCK, HOLDLOCK)
                WHERE source_type = ? AND source_id = ? AND state = ? ORDER BY document_ai_review_id DESC
                """,
                (ps, ignored) -> {
                    ps.setString(1, "ORDER_IMPORT");
                    ps.setLong(2, importId);
                    ps.setInt(3, State.INITIAL.getCode());
                },
                rs -> new ReviewLock(
                    rs.getLong("document_ai_review_id"),
                    rs.getInt("version")
                ), null
            );

        if (rows.isEmpty()) {throw new BusinessException("読取結果がありません。PDFを読み取ってから確認保存してください。");}
        return rows.get(0);
    }

    public void finish(long importId, long orderId, ImportLock importLock, ReviewLock reviewLock, String confirmed, String editor) {
        Map<String, Object> review = new HashMap<>();
        review.put("documentAiReviewId", reviewLock.reviewId());
        review.put("confirmedResult", confirmed);
        review.put("reviewStatus", "CONFIRMED");
        review.put("reviewedBy", editor);
        review.put("reviewedDate", LocalDateTime.now());
        review.put("version", reviewLock.version());
        baseRepository.update(Tables.AI_DOCUMENT_REVIEW_BY_IDS, review, editor);

        Map<String, Object> orderImport = new HashMap<>();
        orderImport.put("orderImportId", importId);
        orderImport.put("orderId", orderId);
        orderImport.put("ocrResult", confirmed);
        orderImport.put("ocrStatus", "COMPLETE");
        orderImport.put("version", importLock.version());
        baseRepository.update(Tables.ORDER_IMPORT_BY_IDS, orderImport, editor);
    }
}
