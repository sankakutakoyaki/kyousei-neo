package com.kyouseipro.neo.domain.business.order.ocr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;

import com.kyouseipro.neo.common.exception.SystemException;
import com.kyouseipro.neo.domain.business.order.ocr.HeiwadoOcrDefaultLayout.OcrRegion;

@Service
public class LocalOcrService {

    /** 帳票設定画面のプレビューと同じ解像度。 */
    private static final int LAYOUT_DPI = 150;
    /** OCR用の画像解像度。帳票設定の座標はこの解像度へ変換して利用する。 */
    private static final int OCR_DPI = 300;

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
            Map<String, OcrRegion> regions) {
        Path workDirectory = null;
        try {
            workDirectory = Files.createTempDirectory("order-ocr-");
            Path prefix = workDirectory.resolve("page");
            execute(List.of(pdftoppmCommand, "-r", Integer.toString(OCR_DPI), "-f", "1", "-singlefile", "-png", pdfPath.toString(), prefix.toString()));
            var page = ImageIO.read(prefix.resolveSibling("page.png").toFile());
            Map<String, String> result = new LinkedHashMap<>();
            for (var entry : regions.entrySet()) {
                var r = scaleForOcr(entry.getValue());
                if (r.x() < 0 || r.y() < 0 || r.width() <= 0 || r.height() <= 0
                        || r.x() + r.width() > page.getWidth()
                        || r.y() + r.height() > page.getHeight()) {
                    throw new SystemException("帳票設定の読取枠がPDFの範囲外です。帳票設定を開いて枠を保存し直してください。", null);
                }
                var image = page.getSubimage(r.x(), r.y(), r.width(), r.height());
                Path cropped = workDirectory.resolve(entry.getKey() + ".png");
                ImageIO.write(image, "png", cropped.toFile());
                result.put(entry.getKey(), execute(createTesseractCommand(entry.getKey(), cropped)).trim());
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
            execute(List.of(pdftoppmCommand, "-r", Integer.toString(LAYOUT_DPI), "-f", "1", "-singlefile", "-png", pdfPath.toString(), prefix.toString()));
            return Files.readAllBytes(workDirectory.resolve("page.png"));
        } catch (IOException e) {
            throw new SystemException("帳票プレビューの作成に失敗しました。", e);
        } finally {
            if (workDirectory != null) deleteRecursively(workDirectory);
        }
    }

    private List<String> createTesseractCommand(String fieldKey, Path image) {
        String pageSegmentationMode = switch (fieldKey) {
            case "address", "contactNote" -> "6";
            default -> "7";
        };
        List<String> command = new ArrayList<>(List.of(
            tesseractCommand,
            image.toString(),
            "stdout",
            "-l", "jpn+eng",
            "--psm", pageSegmentationMode
        ));
        if ("mobilePhone".equals(fieldKey)) {
            command.add("-c");
            command.add("tessedit_char_whitelist=0123456789-");
        }
        return command;
    }

    private OcrRegion scaleForOcr(OcrRegion layoutRegion) {
        double scale = (double) OCR_DPI / LAYOUT_DPI;
        return new OcrRegion(
            (int) Math.round(layoutRegion.x() * scale),
            (int) Math.round(layoutRegion.y() * scale),
            (int) Math.round(layoutRegion.width() * scale),
            (int) Math.round(layoutRegion.height() * scale)
        );
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
