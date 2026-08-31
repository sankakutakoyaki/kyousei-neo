package com.kyouseipro.neo.domain.business.api.order;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SystemException;
import com.kyouseipro.neo.config.UploadConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderPdfImportService {

    private static final byte[] PDF_SIGNATURE = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    private final UploadConfig uploadConfig;

    public OrderPdfImportResult save(String primeConstractorIdValue, MultipartFile file) {
        long primeConstractorId = parsePrimeConstractorId(primeConstractorIdValue);
        validatePdf(file);

        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + ".pdf";
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

        return new OrderPdfImportResult(
            primeConstractorId,
            originalFileName,
            storedFileName
        );
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
}
