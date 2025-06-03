package com.kyouseipro.neo.repository.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.function.ThrowingConsumer;

import com.kyouseipro.neo.interfaceis.sql.SQLConsumer;
import com.kyouseipro.neo.interfaceis.sql.SQLFunction;
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

    public <T> T execSql(Function<Connection, T> execQuery) {
        Connection conn = null;
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, userName, password);
            conn.setAutoCommit(false);

            T result = execQuery.apply(conn);

            conn.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignore) {}
            }
            return null;  // 例外時はnullなど適宜返す
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public <T, R> R execute(
        String sql,
        SqlParameterBinder<T> binder,
        SqlResultExtractor<R> extractor,
        T entity
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            binder.bind(pstmt, entity);

            boolean hasResultSet = pstmt.execute();
            if (hasResultSet) {
                try (ResultSet rs = pstmt.getResultSet()) {
                    return extractor.extract(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T, P> T findOne(
        String sql,
        BiConsumer<PreparedStatement, P> paramSetter,
        Function<ResultSet, T> resultMapper,
        P param
    ) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 正しく paramSetter を使う
            paramSetter.accept(pstmt, param);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return resultMapper.apply(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> List<T> findAll(
        String sql,
        SQLConsumer<PreparedStatement> parameterSetter,
        SQLFunction<ResultSet, T> resultMapper
    ) {
        List<T> results = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            parameterSetter.accept(pstmt);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(resultMapper.apply(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQL error during findAll: " + e.getMessage(), e);
        }

        return results;
    }

    public int executeUpdate(String sql, ThrowingConsumer<PreparedStatement> binder) {
        try (Connection conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.accept(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // public boolean execSql(Function<Connection, Boolean> execQuery) {
    //     Connection conn = null;
    //     try {
    //         Class.forName(driverName);
    //         conn = DriverManager.getConnection(url, userName, password);
    //         conn.setAutoCommit(false);

    //         boolean result = execQuery.apply(conn);

    //         conn.commit();
    //         return result;
    //     } catch (Exception e) {
    //         System.out.println(e);
    //         if (conn != null) {
    //             try {
    //                 conn.rollback();
    //             } catch (SQLException ignore) {}
    //         }
    //         return false;
    //     } finally {
    //         if (conn != null) {
    //             try {
    //                 conn.close();
    //             } catch (SQLException e) {
    //                 System.out.println(e);
    //             }
    //         }
    //     }
    // }

    // public Entity getEntity(SqlData data) {
    //     try {
    //         Class<?> c = Class.forName(data.getClassPath());
    //         Constructor<?> constructor = c.getDeclaredConstructor();
    //         constructor.setAccessible(true);
    //         Entity entity = (Entity) constructor.newInstance();

    //         boolean success = execSql(conn -> {
    //             try (Statement stmt = conn.createStatement();
    //                 ResultSet rs = stmt.executeQuery(data.getSqlString())) {

    //                 if (rs.next()) {
    //                     entity.setEntity(rs);
    //                     return true;
    //                 }
    //                 return false;
    //             } catch (SQLException ex) {
    //                 ex.printStackTrace();
    //                 return false;
    //             }
    //         });

    //         return success ? entity : null;

    //     } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
    //             InvocationTargetException | NoSuchMethodException ex) {
    //         ex.printStackTrace();
    //     }
    //     return null;
    // }

    // public SimpleData executeSqlWithPrepared(String sql, List<Object> params) {
    //     SimpleData simpleData = new SimpleData();
    //     execSql(conn -> {
    //         try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    //             for (int i = 0; i < params.size(); i++) {
    //                 pstmt.setObject(i + 1, params.get(i));
    //             }
    //             ResultSet rs = pstmt.executeQuery();
    //             if (rs.next()) {
    //                 simpleData.setNumber(rs.getInt("number"));
    //                 simpleData.setText(rs.getString("text"));
    //             }
    //             return true;
    //         } catch (SQLException e) {
    //             simpleData.setNumber(0);
    //             simpleData.setText(e.getMessage());
    //             return false;
    //         }
    //     });
    //     return simpleData;
    // }

    // public boolean execSql(Predicate<Statement> execQuery) {
    //     Connection conn = null;
    //     Statement stmt = null;

    //     try {
    //         Class.forName(driverName);
    //         conn = DriverManager.getConnection(url, userName, password);
    //         conn.setAutoCommit(false);
    //         stmt = conn.createStatement();
    //         boolean result = execQuery.test(stmt);
    //         conn.commit();
    //         return result;
    //     } catch (Exception e) {
    //         System.out.println(e);
    //         if (conn != null) {
    //             try {
    //                 conn.rollback();
    //             } catch (SQLException ignore) {
    //             }
    //         }
    //         return false;
    //     } finally {
    //         try {
    //             if (stmt != null) {
    //                 stmt.close();
    //             }
    //             if (conn != null) {
    //                 conn.close();
    //             }
    //         } catch (SQLException e) {
    //             System.out.println(e);
    //         }
    //     }
    // }
    // /**
    //  * 単独のエンティティを取得する
    //  * @param data
    //  * @return 見つからない場合はNULLを返す
    //  */
    // public Entity getEntity(SqlData data) {
    //     try {
    //         Class<?> c = Class.forName(data.getClassPath());

    //         // 引数なしのコンストラクタを取得
    //         Constructor<?> constructor = c.getDeclaredConstructor();

    //         // アクセス許可（protected/private の場合）
    //         constructor.setAccessible(true);

    //         // インスタンス生成
    //         Entity entity = (Entity) constructor.newInstance();

    //         execSql(s -> {
    //             try {
    //                 ResultSet rs = s.executeQuery(data.getSqlString());
    //                 if (rs.next()) {
    //                     entity.setEntity(rs);
    //                 }
    //                 return true;
    //             } catch (SQLException ex) {
    //                 ex.printStackTrace();
    //                 return false;
    //             }
    //         });

    //         return entity;

    //     } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
    //             InvocationTargetException | NoSuchMethodException ex) {
    //         ex.printStackTrace();
    //     }

    //     return null;
    // }

    // /**
    //  * エンティティを保存・更新する
    //  * @param str
    //  * @return コードとメッセージを返す
    //  */
    // public SimpleData excuteSqlString(String str) {
    //     SimpleData simpleData = new SimpleData();
    //     execSql(s -> {
    //         try {
    //             ResultSet rs = s.executeQuery(str);
    //             if (rs.next()){
    //                 simpleData.setNumber(rs.getInt("number"));
    //                 simpleData.setText(rs.getString("text"));
    //             }
    //             return true;
    //         } catch (SQLException e) {
    //             System.out.println(e);
    //             simpleData.setNumber(0);
    //             simpleData.setText(e.getMessage());
    //             return false;
    //         }
    //     });
    //     return simpleData;    
    // }

    // /**
    //  * エンティティリストを取得する
    //  * @param data
    //  * @return リストを返す
    //  */
    // public List<Entity> getEntityList(SqlData data) {
    //     List<Entity> list = new ArrayList<>();
    //     execSql(s -> {
    //         try {
    //             ResultSet rs = s.executeQuery(data.getSqlString());
    
    //             // 毎ループでクラスをロードするのは非効率なので、事前に取得
    //             Class<?> c = Class.forName(data.getClassPath());
    //             Constructor<?> constructor = c.getDeclaredConstructor();
    //             constructor.setAccessible(true);
    
    //             while (rs.next()) {
    //                 Entity ent = (Entity) constructor.newInstance();
    //                 ent.setEntity(rs);
    //                 list.add(ent);
    //             }
    
    //             return true;
    
    //         } catch (SQLException | ClassNotFoundException | IllegalAccessException |
    //                  InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
    //             ex.printStackTrace();
    //             return false;
    //         }
    //     });
    //     return list;
    // }
}