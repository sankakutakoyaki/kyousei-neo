package com.kyouseipro.neo.domain.business.order.ocr.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.sql.*;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.sql.repository.SqlRepository;

class OrderOcrLogRepositoryTest {
    @Test
    void linkUsesTransactionConnectionAndRollsBackWithTheOrder() throws Exception {
        var ds = mock(DataSource.class);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(ds.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.execute()).thenReturn(true);
        when(statement.getResultSet()).thenReturn(results);
        when(results.next()).thenReturn(true, false);
        when(results.getLong(1)).thenReturn(7L);
        var repository = new OrderOcrLogRepository(new SqlRepository(ds), new ObjectMapper());
        var transaction = new TransactionTemplate(new DataSourceTransactionManager(ds));
        assertThrows(IllegalStateException.class, () -> transaction.execute(status -> {
            repository.link(7, 1001, 1085);
            throw new IllegalStateException("order save failed");
        }));
        verify(ds, times(1)).getConnection();
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).prepareStatement(argThat(sql -> sql.contains("order_id IS NULL") && !sql.contains("ai_result_json")));
        verify(statement).setLong(1, 1001);
    }
}
