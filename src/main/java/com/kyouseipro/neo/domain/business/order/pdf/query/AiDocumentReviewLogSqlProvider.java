package com.kyouseipro.neo.domain.business.order.pdf.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("ai_document_reviews")
public class AiDocumentReviewLogSqlProvider
        implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            document_ai_review_id BIGINT,
            document_type NVARCHAR(50),
            source_type NVARCHAR(50),
            source_id BIGINT,
            prime_constractor_id BIGINT,
            ai_engine NVARCHAR(100),
            ai_model NVARCHAR(100),
            prompt_version NVARCHAR(100),
            ai_result NVARCHAR(MAX),
            confirmed_result NVARCHAR(MAX),
            review_status NVARCHAR(30),
            reviewed_by NVARCHAR(100),
            reviewed_date DATETIME2(7),
            regist_date DATETIME2(7),
            update_date DATETIME2(7),
            version INT,
            state INT
            );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT
                INSERTED.document_ai_review_id,
                INSERTED.document_type,
                INSERTED.source_type,
                INSERTED.source_id,
                INSERTED.prime_constractor_id,
                INSERTED.ai_engine,
                INSERTED.ai_model,
                INSERTED.prompt_version,
                INSERTED.ai_result,
                INSERTED.confirmed_result,
                INSERTED.review_status,
                INSERTED.reviewed_by,
                INSERTED.reviewed_date,
                INSERTED.regist_date,
                INSERTED.update_date,
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
            INSERT INTO ai_document_reviews_log (
                document_ai_review_id,
                editor,
                process,
                log_date,
                document_type,
                source_type,
                source_id,
                prime_constractor_id,
                ai_engine,
                ai_model,
                prompt_version,
                ai_result,
                confirmed_result,
                review_status,
                reviewed_by,
                reviewed_date,
                regist_date,
                update_date,
                version,
                state
            )
            SELECT
                document_ai_review_id,
                ?,
                ?,
                CURRENT_TIMESTAMP,
                document_type,
                source_type,
                source_id,
                prime_constractor_id,
                ai_engine,
                ai_model,
                prompt_version,
                ai_result,
                confirmed_result,
                review_status,
                reviewed_by,
                reviewed_date,
                regist_date,
                update_date,
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