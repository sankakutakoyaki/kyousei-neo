package com.kyouseipro.neo.sql.repository;

import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.interfaces.sql.SQLBiConsumer;
import com.kyouseipro.neo.interfaces.sql.SqlParameterBinder;
import com.kyouseipro.neo.interfaces.sql.SqlResultExtractor;
import com.kyouseipro.neo.sql.common.SqlExceptionMapper;
import com.kyouseipro.neo.sql.common.SqlUtil;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SqlRepository {

    private final DataSource dataSource;

    // 必須単一取得（OUTPUT対応）
    public <P, R> R queryOne(
            String sql,
            SqlParameterBinder<P> binder,
            SqlResultExtractor<R> extractor,
            P param
    ) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            if (binder != null) {
                binder.bind(ps, param);
            }

            boolean hasResult = ps.execute();
            while (!hasResult && ps.getUpdateCount() != -1) {
                hasResult = ps.getMoreResults();
            }

            if (!hasResult) {
                throw new RuntimeException("対象データが存在しません");
            }

            try (ResultSet rs = ps.getResultSet()) {
                if (!rs.next()) {
                    throw new RuntimeException("対象データが存在しません");
                }
                R result = extractor.extract(rs);
                if (rs.next()) {
                    throw new IllegalStateException("結果が複数行あります");
                }
                return result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL実行エラー", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public <P, R> R queryOne(String sql, SqlParameterBinder<P> binder, SqlResultExtractor<R> extractor) {
        return this.<P, R>queryOne(sql, binder, extractor, null);
    }

    // 任意単一取得（null許容）
    public <P, R> R queryOneOrNull(
            String sql,
            SqlParameterBinder<P> binder,
            SqlResultExtractor<R> extractor,
            P param
    ) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(ps, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                R result = extractor.extract(rs);
                if (rs.next()) throw new IllegalStateException("結果が複数行あります");
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL実行エラー", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public <P, R> R queryOneOrNull(String sql, SqlParameterBinder<P> binder, SqlResultExtractor<R> extractor) {
        return this.<P, R>queryOneOrNull(sql, binder, extractor, null);
    }

    // 一覧取得
    public <P, R> List<R> queryList(
            String sql,
            SqlParameterBinder<P> binder,
            SqlResultExtractor<R> extractor,
            P param
    ) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (binder != null) binder.bind(ps, param);
            try (ResultSet rs = ps.executeQuery()) {
                List<R> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(extractor.extract(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL実行エラー", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public <P, R> List<R> queryList(String sql, SqlParameterBinder<P> binder, SqlResultExtractor<R> extractor) {
        return this.<P, R>queryList(sql, binder, extractor, null);
    }

    // 更新・削除（UPDATE/DELETE）
    public <P> int update(String sql, SqlParameterBinder<P> binder, P param) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (binder != null) binder.bind(ps, param);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(String sql, SqlParameterBinder<Void> binder) {
        return this.<Void>update(sql, binder, null);
    }

    public <P> int updateRequired(
            String sql,
            SqlParameterBinder<P> binder,
            P param
    ) {
        return updateRequired(
            sql,
            binder,
            param,
            "他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。"
        );
    }

    public int updateRequired(String sql, SqlParameterBinder<Void> binder) {
        return this.<Void>updateRequired(sql, binder, null);
    }

    //　メッセージ可変
    public <P> int updateRequired(
            String sql,
            SqlParameterBinder<P> binder,
            P param,
            String errorMessage
    ) {
        int count = update(sql, binder, param);

        if (count == 0) {
            throw new BusinessException(errorMessage);
        }

        return count;
    }

    public int update(String sql, List<Object> params) {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int updateRequired(String sql, List<Object> params, String errorMessage) {
        int count = update(sql, params);
        if (count == 0) {
            throw new BusinessException(errorMessage);
        }
        return count;
    }

    // バッチ更新・削除
    public <P> int batch(String sql, SQLBiConsumer<PreparedStatement, P> binder, List<P> entities) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (P entity : entities) {
                binder.accept(ps, entity);
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            return Arrays.stream(results).sum();

        } catch (SQLException e) {
            throw new RuntimeException("SQLバッチ実行エラー", e);
        }
    }

    // INSERT + OUTPUT（自動生成ID取得）
    public <P, R> R insert(String sql, SqlParameterBinder<P> binder, SqlResultExtractor<R> extractor, P param) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            if (binder != null) binder.bind(ps, param);

            boolean hasResult = ps.execute();
            while (!hasResult && ps.getUpdateCount() != -1) {
                hasResult = ps.getMoreResults();
            }

            if (!hasResult) {
                throw new RuntimeException("INSERTに失敗しました");
            }

            try (ResultSet rs = ps.getResultSet()) {
                if (!rs.next()) throw new RuntimeException("ID取得に失敗しました");
                R id = extractor.extract(rs);
                if (rs.next()) throw new IllegalStateException("ID取得結果が複数行です");
                return id;
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQL実行エラー", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public <P> boolean exists(
            String sql,
            SqlParameterBinder<P> binder,
            P param
    ) {
        return queryOne(
            sql,
            binder,
            rs -> true,
            param
        );
    }

    public <R> R insert(String sql, List<Object> params, SqlResultExtractor<R> extractor) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);

            boolean hasResult = ps.execute();
            while (!hasResult && ps.getUpdateCount() != -1) {
                hasResult = ps.getMoreResults();
            }
            if (!hasResult) {
                throw new RuntimeException("INSERTに失敗しました");
            }
            try (ResultSet rs = ps.getResultSet()) {
                if (!rs.next()) throw new RuntimeException("ID取得に失敗しました");
                R id = extractor.extract(rs);
                if (rs.next()) throw new IllegalStateException("ID取得結果が複数行です");
                return id;
            }
        } catch (SQLException e) {
            throw SqlExceptionMapper.map(e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    private void setParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    public List<Map<String, Object>> selectMap(String sql, List<Object> params) {
        return queryList(
            sql,
            (ps, p) -> setParams(ps, p),
            rs -> {
                ResultSetMetaData meta = rs.getMetaData();
                Map<String, Object> row = new LinkedHashMap<>();

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String column = SqlUtil.toCamel(meta.getColumnLabel(i));
                    Object value = rs.getObject(i);

                    row.put(column, value);
                }
                return row;
            },
            params
        );
    }
}