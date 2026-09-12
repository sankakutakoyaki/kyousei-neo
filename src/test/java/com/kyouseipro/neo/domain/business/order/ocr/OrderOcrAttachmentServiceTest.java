package com.kyouseipro.neo.domain.business.order.ocr;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.nio.file.*;
import java.sql.ResultSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.kyouseipro.neo.config.UploadConfig;
import com.kyouseipro.neo.domain.attachment.repository.AttachmentRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.interfaces.sql.SqlResultExtractor;

class OrderOcrAttachmentServiceTest {
    @TempDir Path root;

    @Test
    void attachesPdfAndRemovesCopyOnRollbackWithoutRemovingOriginal() throws Exception {
        var sql = mock(SqlRepository.class);
        var attachments = mock(AttachmentRepository.class);
        var rs = mock(ResultSet.class);
        when(rs.getString(1)).thenReturn("伝票.pdf");
        when(rs.getString(2)).thenReturn("original.pdf");
        when(sql.queryOne(anyString(), any(), any(), isNull())).thenAnswer(invocation ->
            ((SqlResultExtractor<?>) invocation.getArgument(2)).extract(rs));
        when(attachments.insertGroup("ORDER", 1001, "取込PDF")).thenReturn(55L);
        Files.writeString(root.resolve("original.pdf"), "%PDF-test");
        var service = new OrderOcrAttachmentService(sql, attachments, new UploadConfig(root.toString()));
        TransactionSynchronizationManager.initSynchronization();
        try {
            service.attach(7, 1001);
            Path copy;
            try (var files = Files.list(root.resolve("attachments/55"))) { copy = files.findFirst().orElseThrow(); }
            assertEquals("%PDF-test", Files.readString(copy));
            verify(attachments).insertFile(eq(55L), anyString(), eq("伝票.pdf"), eq("PDF"), eq("application/pdf"), eq(9L), isNull(), isNull());
            for (var sync : TransactionSynchronizationManager.getSynchronizations())
                sync.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
            assertFalse(Files.exists(copy));
            assertTrue(Files.exists(root.resolve("original.pdf")));
        } finally { TransactionSynchronizationManager.clearSynchronization(); }
    }
}
