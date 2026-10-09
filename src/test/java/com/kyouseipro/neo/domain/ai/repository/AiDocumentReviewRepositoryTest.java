package com.kyouseipro.neo.domain.ai.repository;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import com.kyouseipro.neo.sql.repository.SqlRepository;

class AiDocumentReviewRepositoryTest {
    @Disabled("実装変更に伴いテスト再設計予定")
    @Test
    void confirmsReviewWhenReviewerIsNotProvided() {
        SqlRepository sql = mock(SqlRepository.class);
        new AiDocumentReviewRepository(sql).confirmLatest("ORDER_IMPORT", 13L, "{}", null);
        verify(sql).updateRequired(anyString(), eq(Arrays.<Object>asList("{}", null, "ORDER_IMPORT", 13L, 0)), anyString());
    }
}
