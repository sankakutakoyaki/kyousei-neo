package com.kyouseipro.neo.domain.business.order.pdf.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("order_imports")
public class OrderImportLogSqlProvider
        implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            order_import_id INT,
            order_id INT,
            prime_constractor_id INT,
            original_file_name NVARCHAR(255),
            stored_file_name NVARCHAR(255),
            file_path NVARCHAR(500),
            mime_type NVARCHAR(100),
            file_size BIGINT,
            regist_date DATETIME2(7),
            update_date DATETIME2(7),
            ocr_status NVARCHAR(20),
            ocr_result NVARCHAR(MAX),
            ocr_error NVARCHAR(1000),
            ocr_finished_date DATETIME2(7),
            version INT,
            state INT
            );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT
                INSERTED.order_import_id,
                INSERTED.order_id,
                INSERTED.prime_constractor_id,
                INSERTED.original_file_name,
                INSERTED.stored_file_name,
                INSERTED.file_path,
                INSERTED.mime_type,
                INSERTED.file_size,
                INSERTED.regist_date,
                INSERTED.update_date,
                INSERTED.ocr_status,
                INSERTED.ocr_result,
                INSERTED.ocr_error,
                INSERTED.ocr_finished_date,
                INSERTED.version,
                INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(
            String tableVar,
            String action
    ) {
        return """
            INSERT INTO order_imports_log (
                order_import_id,
                editor,
                process,
                log_date,
                order_id,
                prime_constractor_id,
                original_file_name,
                stored_file_name,
                file_path,
                mime_type,
                file_size,
                regist_date,
                update_date,
                ocr_status,
                ocr_result,
                ocr_error,
                ocr_finished_date,
                version,
                state
            )
            SELECT
                order_import_id,
                ?,
                ?,
                CURRENT_TIMESTAMP,
                order_id,
                prime_constractor_id,
                original_file_name,
                stored_file_name,
                file_path,
                mime_type,
                file_size,
                regist_date,
                update_date,
                ocr_status,
                ocr_result,
                ocr_error,
                ocr_finished_date,
                version,
                state
            FROM %s;
            """.formatted(tableVar);
    }

    @Override
    public List<Object> buildLogParams(
            Map<String,Object> req,
            String action
    ) {
        Object editor =
            req.get("editor");

        if(editor == null){
            throw new IllegalArgumentException(
                "editorが設定されていません"
            );
        }

        return List.of(
            editor,
            action
        );
    }
}