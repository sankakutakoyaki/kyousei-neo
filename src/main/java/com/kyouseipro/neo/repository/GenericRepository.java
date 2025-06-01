package com.kyouseipro.neo.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.interfaceis.sql.PreparedStatementSetter;

public class GenericRepository {
    private final SqlRepository sqlRepository;

    public GenericRepository(SqlRepository sqlRepository) {
        this.sqlRepository = sqlRepository;
    }

    // 1件取得
    public <T extends Entity> T findOne(String sql, PreparedStatementSetter setter, Supplier<T> entitySupplier) {
        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                setter.setValues(pstmt);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        T entity = entitySupplier.get();
                        entity.setEntity(rs);
                        return entity;
                    }
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // 複数件取得
    public <T extends Entity> List<T> findAll(String sql, PreparedStatementSetter setter, Supplier<T> entitySupplier) {
        return sqlRepository.execSql(conn -> {
            List<T> resultList = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                setter.setValues(pstmt);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        T entity = entitySupplier.get();
                        entity.setEntity(rs);
                        resultList.add(entity);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultList;
        });
    }
}

