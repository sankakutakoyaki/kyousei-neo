package com.kyouseipro.neo.domain.business.order.ocr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SystemException;
import com.kyouseipro.neo.config.UploadConfig;
import com.kyouseipro.neo.domain.attachment.repository.AttachmentRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class OrderOcrAttachmentService {
    private final SqlRepository sql;
    private final AttachmentRepository attachments;
    private final UploadConfig uploadConfig;

    private record Source(String originalName, String path) {}

    // The caller has linked and locked the OCR log in the order-save transaction.
    @Transactional(propagation = Propagation.MANDATORY)
    public void attach(long logId, long orderId) {
        Source source = sql.queryOne("""
            SELECT i.original_file_name, i.file_path
            FROM order_ocr_logs l JOIN order_imports i
              ON i.stored_file_name = l.stored_file_name
              AND i.prime_constractor_id = l.prime_constractor_id
            WHERE l.ocr_log_id = ? AND l.order_id = ? AND l.state = 0 AND i.state = 0
            """, (ps, unused) -> { ps.setLong(1, logId); ps.setLong(2, orderId); },
            rs -> new Source(rs.getString(1), rs.getString(2)), null);
        Path root = uploadConfig.getUploadDirectory().toAbsolutePath().normalize();
        Path original = root.resolve(source.path()).normalize();
        if (!original.startsWith(root) || !Files.isRegularFile(original))
            throw new BusinessException("取込PDFの原本が見つかりません。受注は保存されていません。");
        long groupId = attachments.insertGroup("ORDER", orderId, "取込PDF");
        String stored = UUID.randomUUID() + ".pdf";
        Path folder = root.resolve("attachments").resolve(Long.toString(groupId));
        Path destination = folder.resolve(stored);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) cleanup(destination, folder);
            }
        });
        try {
            Files.createDirectories(folder);
            Files.copy(original, destination);
            attachments.insertFile(groupId, stored, source.originalName(), "PDF", "application/pdf",
                Files.size(destination), null, null);
        } catch (IOException error) {
            cleanup(destination, folder);
            throw new SystemException("取込PDFを添付へ保存できませんでした。", error);
        } catch (RuntimeException error) {
            cleanup(destination, folder);
            throw error;
        }
    }

    private void cleanup(Path file, Path folder) {
        try { Files.deleteIfExists(file); } catch (IOException ignored) {}
        try { Files.deleteIfExists(folder); } catch (IOException ignored) {}
    }
}
