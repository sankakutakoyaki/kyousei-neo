package com.kyouseipro.neo.dto.sql.repository;

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;
// import java.util.function.BiConsumer;
// import java.util.function.Function;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Repository;
// import org.springframework.util.function.ThrowingConsumer;

// import com.kyouseipro.neo.interfaces.sql.SQLBiConsumer;
// import com.kyouseipro.neo.interfaces.sql.SqlParameterBinder;
// import com.kyouseipro.neo.interfaces.sql.SqlResultExtractor;

// @Repository
// public class SqlRepository {
//     @Value("${spring.datasource.driver-class-name}")
//     private String driverName;

//     @Value("${spring.datasource.url}")
//     private String url;

//     @Value("${spring.datasource.username}")
//     private String userName;

//     @Value("${spring.datasource.password}")
//     private String password;

//     // SQL Server: skip update counts to reach final ResultSet

//     public <T, R> Optional<R> executeQuery(
//             String sql,
//             SqlParameterBinder<T> binder,
//             SqlResultExtractor<R> extractor,
//             T entity
//     ) {
//         try (Connection conn = DriverManager.getConnection(url, userName, password);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {

//             binder.bind(pstmt, entity);

//             boolean hasResultSet = pstmt.execute();

//             while (!hasResultSet && pstmt.getUpdateCount() != -1) {
//                 hasResultSet = pstmt.getMoreResults();
//             }

//             if (hasResultSet) {
//                 try (ResultSet rs = pstmt.getResultSet()) {
//                     return Optional.ofNullable(extractor.extract(rs));
//                 }
//             }

//             return Optional.empty();

//         } catch (SQLException e) {
//             throw new RuntimeException("SQL実行エラー", e);
//         }
//     }

//     public <T, R> R executeRequired(
//             String sql,
//             SqlParameterBinder<T> binder,
//             SqlResultExtractor<R> extractor,
//             T entity
//     ) {
//         try (Connection conn = DriverManager.getConnection(url, userName, password);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {

//             binder.bind(pstmt, entity);

//             boolean hasResultSet = pstmt.execute();

//             while (!hasResultSet && pstmt.getUpdateCount() != -1) {
//                 hasResultSet = pstmt.getMoreResults();
//             }

//             if (!hasResultSet) {
//                 throw new IllegalStateException("結果セットが取得できません");
//             }

//             try (ResultSet rs = pstmt.getResultSet()) {
//                 R result = extractor.extract(rs);
//                 if (result == null) {
//                     throw new IllegalStateException("必須結果が null です");
//                 }
//                 return result;
//             }

//         } catch (SQLException e) {
//             throw new RuntimeException(e.getMessage(), e);
//         }
//     }

//     public int executeUpdate(
//         String sql,
//         ThrowingConsumer<PreparedStatement> binder
//     ) {
//         try (Connection conn = DriverManager.getConnection(url, userName, password);
//             PreparedStatement ps = conn.prepareStatement(sql)) {

//             binder.accept(ps);
//             return ps.executeUpdate();

//         } catch (SQLException e) {
//             throw new RuntimeException(e.getMessage(), e);
//         }
//     }

//     public <T, P> Optional<T> findOne(
//         String sql,
//         BiConsumer<PreparedStatement, P> paramSetter,
//         Function<ResultSet, T> mapper,
//         P param
//     ) {
//         try (Connection conn = DriverManager.getConnection(url, userName, password);
//             PreparedStatement ps = conn.prepareStatement(sql)) {

//             paramSetter.accept(ps, param);

//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) {
//                     return Optional.ofNullable(mapper.apply(rs));
//                 }
//                 return Optional.empty();
//             }

//         } catch (SQLException e) {
//             throw new RuntimeException("SQL error in findOne", e);
//         }
//     }

//     public <R> List<R> findAll(
//         String sql,
//         SqlParameterBinder<Void> binder,
//         SqlResultExtractor<R> extractor
//     ) {
//         try (
//             Connection conn = DriverManager.getConnection(url, userName, password);
//             PreparedStatement ps = conn.prepareStatement(sql)
//         ) {
//             binder.bind(ps, null);

//             try (ResultSet rs = ps.executeQuery()) {
//                 List<R> list = new ArrayList<>();
//                 while (rs.next()) {
//                     list.add(extractor.extract(rs));
//                 }
//                 return list;
//             }

//         } catch (SQLException e) {
//             throw new RuntimeException(e.getMessage(), e);
//         }
//     }

//     public <T> int executeBatch(
//         String sql,
//         SQLBiConsumer<PreparedStatement, T> binder,
//         List<T> entities
//     ) {
//         try (Connection conn = DriverManager.getConnection(url, userName, password);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {

//             for (T entity : entities) {
//                 binder.accept(pstmt, entity); // パラメータをセット
//                 pstmt.addBatch();             // バッチに追加
//             }

//             int[] results = pstmt.executeBatch(); // まとめて実行
//             return Arrays.stream(results).sum();  // 更新件数の合計を返す

//         } catch (SQLException e) {
//             throw new RuntimeException(e);
//         }
//     }
// }

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.function.ThrowingConsumer;

@Repository
public class SqlRepository {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    /* =========================================================
       基本Query（すべての土台）
       ========================================================= */
    public <R> R query(
            String sql,
            ThrowingConsumer<PreparedStatement> binder,
            ResultSetExtractor<R> extractor
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (binder != null) {
                binder.accept(ps);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return extractor.extract(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQL実行エラー", e);
        }
    }

    /* =========================================================
       単一取得（Optional）
       ========================================================= */
    public <T> Optional<T> queryOne(
            String sql,
            ThrowingConsumer<PreparedStatement> binder,
            Function<ResultSet, T> mapper
    ) {
        return query(sql, binder, rs -> {
            if (rs.next()) {
                return Optional.ofNullable(mapper.apply(rs));
            }
            return Optional.empty();
        });
    }

    /* =========================================================
       List取得
       ========================================================= */
    public <T> List<T> queryList(
            String sql,
            ThrowingConsumer<PreparedStatement> binder,
            Function<ResultSet, T> mapper
    ) {
        return query(sql, binder, rs -> {
            List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapper.apply(rs));
            }
            return list;
        });
    }

    /* =========================================================
       exists（COUNT不要）
       ========================================================= */
    public boolean exists(
            String sql,
            ThrowingConsumer<PreparedStatement> binder
    ) {
        return query(sql, binder, ResultSet::next);
    }

    /* =========================================================
       update / insert / delete
       ========================================================= */
    public int update(
            String sql,
            ThrowingConsumer<PreparedStatement> binder
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (binder != null) {
                binder.accept(ps);
            }

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SQL更新エラー", e);
        }
    }

    /* =========================================================
       バッチ
       ========================================================= */
    public <T> int batch(
            String sql,
            BatchBinder<T> binder,
            List<T> entities
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (T entity : entities) {
                binder.bind(ps, entity);
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            int sum = 0;
            for (int r : results) {
                sum += r;
            }
            return sum;

        } catch (SQLException e) {
            throw new RuntimeException("SQLバッチエラー", e);
        }
    }

    /* =========================================================
       内部FunctionalInterface
       ========================================================= */
    @FunctionalInterface
    public interface ResultSetExtractor<R> {
        R extract(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    public interface BatchBinder<T> {
        void bind(PreparedStatement ps, T entity) throws SQLException;
    }
}