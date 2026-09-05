package com.kyouseipro.neo.domain.ai.application;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.exception.SystemException;
import com.kyouseipro.neo.domain.ai.model.AiDocumentReview;
import com.kyouseipro.neo.domain.ai.repository.AiDocumentReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 文書AIの回答と人の確認結果を保存する共通サービス。
 * ORDER_FAX、EXPENSE_RECEIPT、PHOTOなど、将来の文書種別を共通形式で扱う。
 */
@Service
@RequiredArgsConstructor
public class AiLearningDataService {

    private final AiDocumentReviewRepository aiDocumentReviewRepository;
    private final ObjectMapper objectMapper;

    public long recordCandidate(
            String documentType,
            String sourceType,
            long sourceId,
            Long primeConstractorId,
            String aiEngine,
            String aiModel,
            String promptVersion,
            Map<String, String> candidate) {
        return aiDocumentReviewRepository.insert(
            documentType,
            sourceType,
            sourceId,
            primeConstractorId,
            aiEngine,
            aiModel,
            promptVersion,
            toJson(candidate)
        );
    }

    public void confirmLatest(
            String sourceType,
            long sourceId,
            Map<String, String> confirmedResult) {
        aiDocumentReviewRepository.confirmLatest(
            sourceType,
            sourceId,
            toJson(confirmedResult),
            null
        );
    }

    /** 将来の追加学習・精度評価用。確定済みデータだけを返す。 */
    public List<AiDocumentReview> findConfirmedForTraining(String documentType) {
        return aiDocumentReviewRepository.findConfirmedForTraining(documentType);
    }

    private String toJson(Map<String, String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            throw new SystemException("AI学習用データを保存形式に変換できませんでした。", e);
        }
    }
}
