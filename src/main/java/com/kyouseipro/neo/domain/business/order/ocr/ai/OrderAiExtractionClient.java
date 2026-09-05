package com.kyouseipro.neo.domain.business.order.ocr.ai;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.kyouseipro.neo.common.exception.SystemException;

/**
 * GPUサーバー上の伝票読取サービスとの接続を担当する。
 *
 * {@code order.ai.enabled=false} の間は外部サービスを呼び出さず、従来OCRを利用する。
 */
@Component
public class OrderAiExtractionClient {

    private final boolean enabled;
    private final String extractionUrl;
    private final String modelName;
    private final RestClient restClient;

    public OrderAiExtractionClient(
            @Value("${order.ai.enabled:false}") boolean enabled,
            @Value("${order.ai.extraction-url:http://127.0.0.1:18080/v1/document-extractions}") String extractionUrl,
            @Value("${order.ai.model-name:qwen2.5vl:3b}") String modelName,
            RestClient.Builder restClientBuilder) {
        this.enabled = enabled;
        this.extractionUrl = extractionUrl;
        this.modelName = modelName;
        this.restClient = restClientBuilder.build();
    }

    public String modelName() {
        return modelName;
    }

    public Optional<Map<String, String>> extract(Path pdfPath, long primeConstractorId) {
        if (!enabled) {
            return Optional.empty();
        }

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new FileSystemResource(pdfPath));
        form.add("primeConstractorId", Long.toString(primeConstractorId));
        form.add("documentType", "ORDER_FAX");

        try {
            ExtractionResponse response = restClient.post()
                .uri(extractionUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(form)
                .retrieve()
                .body(ExtractionResponse.class);
            if (response == null || response.candidates() == null) {
                throw new SystemException("AI読取サービスから結果を受け取れませんでした。", null);
            }
            return Optional.of(response.candidates());
        } catch (SystemException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SystemException("AI読取サービスへの接続に失敗しました。GPU読取サービスが起動しているか確認してください。", e);
        }
    }

    /** AI読取サービスが返すJSON形式。 */
    public record ExtractionResponse(Map<String, String> candidates) {
    }
}
