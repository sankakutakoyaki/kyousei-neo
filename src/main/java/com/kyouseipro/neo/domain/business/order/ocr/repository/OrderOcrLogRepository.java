package com.kyouseipro.neo.domain.business.order.ocr.repository;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderOcrLogRepository {
    private final SqlRepository sql;
    private final ObjectMapper mapper;

    public void lockImport(long id) {
        sql.queryOne("SELECT order_import_id FROM order_imports WITH (UPDLOCK, HOLDLOCK) WHERE order_import_id = ? AND state = 0",
            (ps, unused) -> ps.setLong(1, id), rs -> rs.getLong(1), null);
    }

    public Map<String, Object> find(long importId) {
        return sql.queryOneOrNull("""
            SELECT l.ocr_log_id, l.ai_result_json, l.model_name, l.prompt_version, l.order_id
            FROM dbo.order_ocr_logs l JOIN order_imports i
              ON l.stored_file_name = i.stored_file_name AND l.prime_constractor_id = i.prime_constractor_id
            WHERE i.order_import_id = ? AND l.state = 0
            """, (ps, unused) -> ps.setLong(1, importId), rs -> {
                if (rs.getObject("order_id") != null) throw new BusinessException("このPDFは受注登録済みです。");
                try {
                    Map<String, Object> result = new java.util.LinkedHashMap<>();
                    result.put("ocrLogId", rs.getLong("ocr_log_id"));
                    result.put("candidates", mapper.readTree(rs.getString("ai_result_json")));
                    result.put("modelName", rs.getString("model_name"));
                    result.put("promptVersion", rs.getString("prompt_version"));
                    return result;
                } catch (java.io.IOException e) { throw new BusinessException("OCRログを読み込めませんでした。"); }
            }, null);
    }

    public long insert(long importId, String json, String model, String prompt) {
        return sql.insert("""
            INSERT INTO dbo.order_ocr_logs
                (order_id, prime_constractor_id, original_file_name, stored_file_name, ai_result_json, model_name, prompt_version)
            OUTPUT INSERTED.ocr_log_id
            SELECT NULL, prime_constractor_id, original_file_name, stored_file_name, ?, ?, ?
            FROM order_imports WHERE order_import_id = ? AND state = 0
            """, (ps, unused) -> {
                ps.setString(1, json); ps.setString(2, model); ps.setString(3, prompt); ps.setLong(4, importId);
            }, rs -> rs.getLong(1), null);
    }

    public void requireUnlinked(long logId, long shipperId) {
        Long id = sql.queryOneOrNull("""
            SELECT ocr_log_id FROM dbo.order_ocr_logs WITH (UPDLOCK, HOLDLOCK)
            WHERE ocr_log_id = ? AND order_id IS NULL AND prime_constractor_id = ? AND state = 0
            """, (ps, unused) -> { ps.setLong(1, logId); ps.setLong(2, shipperId); }, rs -> rs.getLong(1), null);
        if (id == null) throw new BusinessException("OCRログが登録済み、または荷主が一致しません。再読み込みしてください。");
    }

    public void link(long logId, long orderId, long shipperId) {
        // queryOne uses the Spring transaction connection; update() uses a separate connection.
        sql.queryOne("""
            UPDATE dbo.order_ocr_logs SET order_id = ?
            OUTPUT INSERTED.ocr_log_id
            WHERE ocr_log_id = ? AND order_id IS NULL AND prime_constractor_id = ? AND state = 0;
            """, (ps, unused) -> {
                ps.setLong(1, orderId); ps.setLong(2, logId); ps.setLong(3, shipperId);
            }, rs -> rs.getLong(1), null);
    }
}
