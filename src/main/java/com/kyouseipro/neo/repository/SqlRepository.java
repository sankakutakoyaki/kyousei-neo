package com.kyouseipro.neo.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.interfaceis.Entity;

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

    public boolean execSql(Function<Connection, Boolean> execQuery) {
        Connection conn = null;
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, userName, password);
            conn.setAutoCommit(false);

            boolean result = execQuery.apply(conn);

            conn.commit();
            return result;
        } catch (Exception e) {
            System.out.println(e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignore) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e);
                }
            }
        }
    }

    public Entity getEntity(SqlData data) {
        try {
            Class<?> c = Class.forName(data.getClassPath());
            Constructor<?> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            Entity entity = (Entity) constructor.newInstance();

            boolean success = execSql(conn -> {
                try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(data.getSqlString())) {

                    if (rs.next()) {
                        entity.setEntity(rs);
                        return true;
                    }
                    return false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }
            });

            return success ? entity : null;

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public SimpleData executeSqlWithPrepared(String sql, List<Object> params) {
        SimpleData simpleData = new SimpleData();
        execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    simpleData.setNumber(rs.getInt("number"));
                    simpleData.setText(rs.getString("text"));
                }
                return true;
            } catch (SQLException e) {
                simpleData.setNumber(0);
                simpleData.setText(e.getMessage());
                return false;
            }
        });
        return simpleData;
    }

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