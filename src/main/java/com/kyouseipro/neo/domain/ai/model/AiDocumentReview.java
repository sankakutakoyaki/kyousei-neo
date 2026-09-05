package com.kyouseipro.neo.domain.ai.model;

import java.time.LocalDateTime;

/**
 * AIの回答と、人が確認して確定した値を対にして保存する学習用の履歴。
 * 元の業務データとは分離し、学習処理が受注・経費データを変更しないようにする。
 */
public record AiDocumentReview(
        long documentAiReviewId,
        String documentType,
        String sourceType,
        long sourceId,
        Long primeConstractorId,
        String aiEngine,
        String aiModel,
        String promptVersion,
        String aiResult,
        String confirmedResult,
        String reviewStatus,
        LocalDateTime reviewedDate) {
}
