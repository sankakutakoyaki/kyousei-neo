package com.kyouseipro.neo.sql.repository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.sql.*;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository;

class OrderHistoryTransactionTest {
    @Test void historyFailureRollsBackOrderAndLogOnSameConnection() throws Exception {
        var source = mock(DataSource.class);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        when(source.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1).thenThrow(new SQLException("history failed"));
        var repository = new SqlRepository(source);
        var history = new OrderHistoryRepository(repository);
        assertThrows(RuntimeException.class, () ->
            new TransactionTemplate(new DataSourceTransactionManager(source)).executeWithoutResult(status -> {
                repository.update("UPDATE orders; INSERT orders_log", List.of());
                history.recordSaved(12L, false, "employee");
            }));
        verify(source, times(1)).getConnection();
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(statement).setObject(1, "ORDER_SAVED");
        verify(statement).setObject(2, "employee");
        verify(statement).setObject(3, 12L);
    }
}
