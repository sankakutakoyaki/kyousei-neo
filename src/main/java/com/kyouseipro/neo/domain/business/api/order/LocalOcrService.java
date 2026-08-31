package com.kyouseipro.neo.domain.business.api.order;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;

import com.kyouseipro.neo.common.exception.SystemException;

@Service
public class LocalOcrService {

    private final String pdftoppmCommand;
    private final String tesseractCommand;

    public LocalOcrService(
            @Value("${ocr.pdftoppm-command:pdftoppm}") String pdftoppmCommand,
            @Value("${ocr.tesseract-command:tesseract}") String tesseractCommand) {
        this.pdftoppmCommand = pdftoppmCommand;
        this.tesseractCommand = tesseractCommand;
    }

    public String extractText(Path pdfPath) {
        Path workDirectory = null;
        try {
            workDirectory = Files.createTempDirectory("order-ocr-");
        } catch (IOException e) {
            throw new SystemException("OCR用の作業領域を作成できませんでした。", e);
        }

        try {
            Path imagePrefix = workDirectory.resolve("page");
            execute(List.of(pdftoppmCommand, "-r", "300", "-png", pdfPath.toString(), imagePrefix.toString()));

            List<Path> pages;
            try (var files = Files.list(workDirectory)) {
                pages = files
                    .filter(path -> path.getFileName().toString().endsWith(".png"))
                    .sorted()
                    .toList();
            }
            if (pages.isEmpty()) {
                throw new SystemException("PDFをOCR用の画像に変換できませんでした。", null);
            }

            StringBuilder text = new StringBuilder();
            for (Path page : pages) {
                if (!text.isEmpty()) {
                    text.append(System.lineSeparator()).append(System.lineSeparator());
                }
                text.append(execute(List.of(
                    tesseractCommand,
                    page.toString(),
                    "stdout",
                    "-l", "jpn+eng",
                    "--psm", "6"
                )));
            }
            return text.toString().trim();
        } catch (IOException e) {
            throw new SystemException("OCR処理に失敗しました。", e);
        } finally {
            deleteRecursively(workDirectory);
        }
    }

    public Map<String, String> extractRegions(
            Path pdfPath,
            Map<String, HeiwadoOcrLayout.OcrRegion> regions) {
        Path workDirectory = null;
        try {
            workDirectory = Files.createTempDirectory("order-ocr-");
            Path prefix = workDirectory.resolve("page");
            execute(List.of(pdftoppmCommand, "-r", "300", "-f", "1", "-singlefile", "-png", pdfPath.toString(), prefix.toString()));
            var page = ImageIO.read(prefix.resolveSibling("page.png").toFile());
            Map<String, String> result = new LinkedHashMap<>();
            for (var entry : regions.entrySet()) {
                var r = entry.getValue();
                var image = page.getSubimage(r.x(), r.y(), r.width(), r.height());
                Path cropped = workDirectory.resolve(entry.getKey() + ".png");
                ImageIO.write(image, "png", cropped.toFile());
                result.put(entry.getKey(), execute(List.of(tesseractCommand, cropped.toString(), "stdout", "-l", "jpn+eng", "--psm", "7")).trim());
            }
            return result;
        } catch (IOException e) {
            throw new SystemException("項目別OCR処理に失敗しました。", e);
        } finally {
            if (workDirectory != null) {
                deleteRecursively(workDirectory);
            }
        }
    }

    public byte[] renderFirstPage(Path pdfPath) {
        Path workDirectory = null;
        try {
            workDirectory = Files.createTempDirectory("order-preview-");
            Path prefix = workDirectory.resolve("page");
            execute(List.of(pdftoppmCommand, "-r", "150", "-f", "1", "-singlefile", "-png", pdfPath.toString(), prefix.toString()));
            return Files.readAllBytes(workDirectory.resolve("page.png"));
        } catch (IOException e) {
            throw new SystemException("帳票プレビューの作成に失敗しました。", e);
        } finally {
            if (workDirectory != null) deleteRecursively(workDirectory);
        }
    }

    private String execute(List<String> command) throws IOException {
        Process process = new ProcessBuilder(command)
            .redirectErrorStream(true)
            .start();
        String output;
        try {
            output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (process.waitFor() != 0) {
                throw new SystemException("OCRコマンドの実行に失敗しました。" + output, null);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SystemException("OCR処理が中断されました。", e);
        }
        return output;
    }

    private void deleteRecursively(Path directory) {
        try (var paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }
}
