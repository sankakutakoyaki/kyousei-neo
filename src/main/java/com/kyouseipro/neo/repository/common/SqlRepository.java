package com.kyouseipro.neo.repository.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.function.ThrowingConsumer;

import com.kyouseipro.neo.interfaceis.sql.SQLBiConsumer;
import com.kyouseipro.neo.interfaceis.sql.SqlParameterBinder;
import com.kyouseipro.neo.interfaceis.sql.SqlResultExtractor;

@Repository
public class SqlRepository {
    @Value("${spring.datasource.driver-class-name}")
    private String driverName;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    // SQL Server: skip update counts to reach final ResultSet

    public <T, R> Optional<R> executeQuery(
            String sql,
            SqlParameterBinder<T> binder,
            SqlResultExtractor<R> extractor,
            T entity
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            binder.bind(pstmt, entity);

            boolean hasResultSet = pstmt.execute();

            while (!hasResultSet && pstmt.getUpdateCount() != -1) {
                hasResultSet = pstmt.getMoreResults();
            }

            if (hasResultSet) {
                try (ResultSet rs = pstmt.getResultSet()) {
                    return Optional.ofNullable(extractor.extract(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("SQL実行エラー", e);
        }
    }

    public <T, R> R executeRequired(
            String sql,
            SqlParameterBinder<T> binder,
            SqlResultExtractor<R> extractor,
            T entity
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            binder.bind(pstmt, entity);

            boolean hasResultSet = pstmt.execute();

            while (!hasResultSet && pstmt.getUpdateCount() != -1) {
                hasResultSet = pstmt.getMoreResults();
            }

            if (!hasResultSet) {
                throw new IllegalStateException("結果セットが取得できません");
            }

            try (ResultSet rs = pstmt.getResultSet()) {
                R result = extractor.extract(rs);
                if (result == null) {
                    throw new IllegalStateException("必須結果が null です");
                }
                return result;
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQL実行エラー", e);
        }
    }

    public int executeUpdate(
        String sql,
        ThrowingConsumer<PreparedStatement> binder
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            binder.accept(ps);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SQL実行エラー", e);
        }
    }

    public <T, P> Optional<T> findOne(
        String sql,
        BiConsumer<PreparedStatement, P> paramSetter,
        Function<ResultSet, T> mapper,
        P param
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            paramSetter.accept(ps, param);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(mapper.apply(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQL error in findOne", e);
        }
    }

    public <R> List<R> findAll(
        String sql,
        SqlParameterBinder<Void> binder,
        SqlResultExtractor<R> extractor
    ) {
        try (
            Connection conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            binder.bind(ps, null);

            try (ResultSet rs = ps.executeQuery()) {
                List<R> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(extractor.extract(rs));
                }
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQL実行エラー", e);
        }
    }

    public <T> int executeBatch(
        String sql,
        SQLBiConsumer<PreparedStatement, T> binder,
        List<T> entities
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (T entity : entities) {
                binder.accept(pstmt, entity); // パラメータをセット
                pstmt.addBatch();             // バッチに追加
            }

            int[] results = pstmt.executeBatch(); // まとめて実行
            return Arrays.stream(results).sum();  // 更新件数の合計を返す

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}