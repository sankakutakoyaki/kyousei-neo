package com.kyouseipro.neo.domain.business.api.order;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderPdfImportRepository {

    private final SqlRepository sqlRepository;

    public long insert(
            long primeConstractorId,
            String originalFileName,
            String storedFileName,
            String filePath,
            String mimeType,
            long fileSize) {
        String sql = """
            INSERT INTO order_imports (
                prime_constractor_id,
                original_file_name,
                stored_file_name,
                file_path,
                mime_type,
                file_size,
                regist_date,
                update_date,
                version,
                state
            )
            OUTPUT INSERTED.order_import_id
            VALUES (?, ?, ?, ?, ?, ?, SYSDATETIME(), SYSDATETIME(), 1, 0)
        """;

        return sqlRepository.insert(
            sql,
            (ps, ignored) -> {
                ps.setLong(1, primeConstractorId);
                ps.setString(2, originalFileName);
                ps.setString(3, storedFileName);
                ps.setString(4, filePath);
                ps.setString(5, mimeType);
                ps.setLong(6, fileSize);
            },
            rs -> rs.getLong("order_import_id"),
            null
        );
    }

    public List<OrderPdfImportListItem> findByPrimeConstractorId(long primeConstractorId) {
        String sql = """
            SELECT
                order_import_id,
                original_file_name,
                file_size,
                regist_date
            FROM order_imports
            WHERE prime_constractor_id = ?
              AND state = 0
            ORDER BY regist_date DESC, order_import_id DESC
        """;

        return sqlRepository.queryList(
            sql,
            (ps, ignored) -> ps.setLong(1, primeConstractorId),
            rs -> new OrderPdfImportListItem(
                rs.getLong("order_import_id"),
                rs.getString("original_file_name"),
                rs.getLong("file_size"),
                rs.getTimestamp("regist_date").toLocalDateTime()
            ),
            null
        );
    }

    public OrderPdfImportFile findFile(long orderImportId) {
        String sql = """
            SELECT original_file_name, file_path
            FROM order_imports
            WHERE order_import_id = ?
              AND state = 0
        """;

        return sqlRepository.queryOneOrNull(
            sql,
            (ps, ignored) -> ps.setLong(1, orderImportId),
            rs -> new OrderPdfImportFile(
                rs.getString("original_file_name"),
                java.nio.file.Path.of(rs.getString("file_path"))
            ),
            null
        );
    }

    public void saveOcrResult(long orderImportId, String result) {
        sqlRepository.updateRequired(
            "UPDATE order_imports SET ocr_status = 'COMPLETE', ocr_result = ?, ocr_error = NULL, ocr_finished_date = SYSDATETIME(), update_date = SYSDATETIME() WHERE order_import_id = ? AND state = 0",
            java.util.List.of(result, orderImportId),
            "OCR結果の保存に失敗しました。"
        );
    }

    public long findPrimeConstractorId(long orderImportId) {
        return sqlRepository.queryOne("SELECT prime_constractor_id FROM order_imports WHERE order_import_id = ? AND state = 0", (ps, ignored) -> ps.setLong(1, orderImportId), rs -> rs.getLong(1), null);
    }
}
