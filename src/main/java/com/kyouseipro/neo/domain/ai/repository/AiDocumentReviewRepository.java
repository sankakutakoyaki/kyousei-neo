package com.kyouseipro.neo.domain.ai.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.domain.ai.model.AiDocumentReview;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AiDocumentReviewRepository {

    private final SqlRepository sqlRepository;

    public long insert(
            String documentType,
            String sourceType,
            long sourceId,
            Long primeConstractorId,
            String aiEngine,
            String aiModel,
            String promptVersion,
            String aiResult) {
        return sqlRepository.insert(
            """
                INSERT INTO ai_document_reviews (
                    document_type, source_type, source_id, prime_constractor_id,
                    ai_engine, ai_model, prompt_version, ai_result,
                    regist_date, update_date, version, state
                )
                OUTPUT INSERTED.document_ai_review_id
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME(), SYSDATETIME(), 1, 0)
            """,
            java.util.Arrays.asList(documentType, sourceType, sourceId, primeConstractorId, aiEngine, aiModel, promptVersion, aiResult),
            rs -> rs.getLong("document_ai_review_id")
        );
    }

    public void confirmLatest(String sourceType, long sourceId, String confirmedResult, String reviewedBy) {
        sqlRepository.updateRequired(
            """
                UPDATE ai_document_reviews
                SET confirmed_result = ?, review_status = 'CONFIRMED', reviewed_by = ?,
                    reviewed_date = SYSDATETIME(), update_date = SYSDATETIME()
                WHERE document_ai_review_id = (
                    SELECT TOP 1 document_ai_review_id
                    FROM ai_document_reviews
                    WHERE source_type = ? AND source_id = ?
                      AND review_status = 'PENDING_REVIEW' AND state = 0
                    ORDER BY document_ai_review_id DESC
                )
            """,
            List.of(confirmedResult, reviewedBy, sourceType, sourceId),
            "確認対象のAI読取結果が見つかりません。OCRを実行し直してください。"
        );
    }

    public List<AiDocumentReview> findConfirmedForTraining(String documentType) {
        return sqlRepository.queryList(
            """
                SELECT document_ai_review_id, document_type, source_type, source_id,
                       prime_constractor_id, ai_engine, ai_model, prompt_version,
                       ai_result, confirmed_result, review_status, reviewed_date
                FROM ai_document_reviews
                WHERE document_type = ? AND review_status = 'CONFIRMED' AND state = 0
                ORDER BY document_ai_review_id
            """,
            (ps, ignored) -> ps.setString(1, documentType),
            rs -> {
                Timestamp reviewedDate = rs.getTimestamp("reviewed_date");
                return new AiDocumentReview(
                    rs.getLong("document_ai_review_id"),
                    rs.getString("document_type"),
                    rs.getString("source_type"),
                    rs.getLong("source_id"),
                    rs.getObject("prime_constractor_id", Long.class),
                    rs.getString("ai_engine"),
                    rs.getString("ai_model"),
                    rs.getString("prompt_version"),
                    rs.getString("ai_result"),
                    rs.getString("confirmed_result"),
                    rs.getString("review_status"),
                    reviewedDate == null ? null : reviewedDate.toLocalDateTime()
                );
            },
            null
        );
    }
}
