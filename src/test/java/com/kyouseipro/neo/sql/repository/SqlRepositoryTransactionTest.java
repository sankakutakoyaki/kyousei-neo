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

class SqlRepositoryTransactionTest {
    @Test void updateAndLogFailureUseSingleTransactionConnectionAndRollback() throws Exception {
        var source = mock(DataSource.class);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        when(source.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1).thenThrow(new SQLException("log failed"));
        var repository = new SqlRepository(source);
        var transaction = new TransactionTemplate(new DataSourceTransactionManager(source));
        assertThrows(RuntimeException.class, () -> transaction.executeWithoutResult(status -> {
            repository.update("UPDATE assignment", List.of());
            repository.update("INSERT log", List.of());
        }));
        verify(source, times(1)).getConnection();
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection, times(1)).close();
    }
    @Test void successfulUpdatesCommitTogether() throws Exception {
        var source = mock(DataSource.class);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        when(source.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);
        var repository = new SqlRepository(source);
        new TransactionTemplate(new DataSourceTransactionManager(source)).executeWithoutResult(status -> {
            repository.update("UPDATE assignment", List.of());
            repository.update("INSERT log", List.of());
        });
        verify(source, times(1)).getConnection();
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).close();
    }
}
