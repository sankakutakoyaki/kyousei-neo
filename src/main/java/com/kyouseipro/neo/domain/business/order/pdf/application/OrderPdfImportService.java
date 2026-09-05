package com.kyouseipro.neo.domain.business.order.pdf.application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SystemException;
import com.kyouseipro.neo.config.UploadConfig;
import com.kyouseipro.neo.domain.ai.application.AiLearningDataService;
import com.kyouseipro.neo.domain.business.order.ocr.HeiwadoOcrDefaultLayout;
import com.kyouseipro.neo.domain.business.order.ocr.LocalOcrService;
import com.kyouseipro.neo.domain.business.order.ocr.ai.OrderAiExtractionClient;
import com.kyouseipro.neo.domain.business.order.ocr.model.OrderOcrLayout;
import com.kyouseipro.neo.domain.business.order.ocr.repository.OrderOcrLayoutRepository;
import com.kyouseipro.neo.domain.business.order.pdf.model.OrderPdfImportFile;
import com.kyouseipro.neo.domain.business.order.pdf.model.OrderPdfImportListItem;
import com.kyouseipro.neo.domain.business.order.pdf.model.OrderPdfImportResult;
import com.kyouseipro.neo.domain.business.order.pdf.repository.OrderPdfImportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderPdfImportService {

    private static final byte[] PDF_SIGNATURE = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    private final UploadConfig uploadConfig;
    private final OrderPdfImportRepository orderPdfImportRepository;
    private final LocalOcrService localOcrService;
    private final OrderAiExtractionClient orderAiExtractionClient;
    private final AiLearningDataService aiLearningDataService;
    private final OrderOcrLayoutRepository orderOcrLayoutRepository;
    private final ObjectMapper objectMapper;

    public OrderPdfImportResult save(String primeConstractorIdValue, MultipartFile file) {
        long primeConstractorId = parsePrimeConstractorId(primeConstractorIdValue);
        validatePdf(file);

        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + ".pdf";
        String relativePath = "order-pdf/" + primeConstractorId + "/" + storedFileName;
        Path directory = uploadConfig.getUploadDirectory()
            .resolve("order-pdf")
            .resolve(Long.toString(primeConstractorId));
        Path destination = directory.resolve(storedFileName).normalize();

        try {
            Files.createDirectories(directory);
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new SystemException("受注伝票PDFの保存に失敗しました。", e);
        }

        long orderImportId;
        try {
            orderImportId = orderPdfImportRepository.insert(
                primeConstractorId,
                originalFileName,
                storedFileName,
                relativePath,
                file.getContentType(),
                file.getSize()
            );
        } catch (RuntimeException e) {
            deleteSavedFile(destination);
            throw e;
        }

        return new OrderPdfImportResult(
            orderImportId,
            primeConstractorId,
            originalFileName,
            storedFileName
        );
    }

    public List<OrderPdfImportListItem> findByPrimeConstractorId(String primeConstractorIdValue) {
        return orderPdfImportRepository.findByPrimeConstractorId(
            parsePrimeConstractorId(primeConstractorIdValue)
        );
    }

    public OrderPdfImportFile findFile(long orderImportId) {
        if (orderImportId <= 0) {
            throw new BusinessException("取込PDFの指定が不正です。");
        }

        OrderPdfImportFile file = orderPdfImportRepository.findFile(orderImportId);
        if (file == null) {
            throw new BusinessException("取込PDFが見つかりません。");
        }

        Path path = uploadConfig.getUploadDirectory().resolve(file.path()).normalize();
        if (!path.startsWith(uploadConfig.getUploadDirectory()) || !Files.isRegularFile(path)) {
            throw new BusinessException("取込PDFが見つかりません。");
        }
        return new OrderPdfImportFile(file.originalFileName(), path);
    }

    @Transactional
    public Map<String, String> recognizeHeiwado(long orderImportId) {
        OrderPdfImportFile file = findFile(orderImportId);
        long primeConstractorId = orderPdfImportRepository.findPrimeConstractorId(orderImportId);
        var aiResult = orderAiExtractionClient.extract(file.path(), primeConstractorId);
        if (aiResult.isPresent()) {
            orderPdfImportRepository.saveOcrResult(orderImportId, toJson(aiResult.get()));
            aiLearningDataService.recordCandidate(
                "ORDER_FAX", "ORDER_IMPORT", orderImportId, primeConstractorId,
                "OLLAMA", orderAiExtractionClient.modelName(), "order-fax-v1", aiResult.get()
            );
            return aiResult.get();
        }
        Map<String, HeiwadoOcrDefaultLayout.OcrRegion> regions = orderOcrLayoutRepository.find(primeConstractorId).stream()
            .collect(java.util.stream.Collectors.toMap(OrderOcrLayout::fieldKey, layout -> new HeiwadoOcrDefaultLayout.OcrRegion(layout.x(), layout.y(), layout.width(), layout.height())));
        if (regions.isEmpty()) regions = HeiwadoOcrDefaultLayout.REGIONS;
        Map<String, String> result = localOcrService.extractRegions(file.path(), regions);
        orderPdfImportRepository.saveOcrResult(orderImportId, toJson(result));
        aiLearningDataService.recordCandidate(
            "ORDER_FAX", "ORDER_IMPORT", orderImportId, primeConstractorId,
            "TESSERACT", null, "order-fax-v1", result
        );
        return result;
    }

    @Transactional
    public void saveCandidate(long orderImportId, Map<String, String> candidate) {
        findFile(orderImportId);
        orderPdfImportRepository.saveOcrResult(orderImportId, toJson(candidate));
        aiLearningDataService.confirmLatest("ORDER_IMPORT", orderImportId, candidate);
    }

    public byte[] preview(long orderImportId) {
        return localOcrService.renderFirstPage(findFile(orderImportId).path());
    }

    private long parsePrimeConstractorId(String value) {
        if (value == null || value.isBlank() || "0".equals(value.trim())) {
            throw new BusinessException("荷主を選択してください。");
        }

        try {
            long primeConstractorId = Long.parseLong(value.trim());
            if (primeConstractorId <= 0) {
                throw new BusinessException("荷主を選択してください。");
            }
            return primeConstractorId;
        } catch (NumberFormatException e) {
            throw new BusinessException("荷主の指定が不正です。");
        }
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("PDFファイルを選択してください。");
        }
        if (!MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(file.getContentType())) {
            throw new BusinessException("PDFファイルを選択してください。");
        }

        try (InputStream input = file.getInputStream()) {
            byte[] signature = input.readNBytes(PDF_SIGNATURE.length);
            if (!java.util.Arrays.equals(PDF_SIGNATURE, signature)) {
                throw new BusinessException("PDFファイルを選択してください。");
            }
        } catch (IOException e) {
            throw new SystemException("PDFファイルを確認できませんでした。", e);
        }
    }

    private void deleteSavedFile(Path destination) {
        try {
            Files.deleteIfExists(destination);
        } catch (IOException e) {
            // DB登録失敗時に元の例外を優先する。
        }
    }

    private String toJson(Map<String, String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            throw new SystemException("OCR結果を保存形式に変換できませんでした。", e);
        }
    }
}
