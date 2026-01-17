package com.kyouseipro.neo.ai.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.ai.entity.AiInsertParam;

public class AiParameterBinder {
    
    public static void findForQuestion(PreparedStatement ps, String q) throws SQLException {
        String like = "%" + q + "%";
        ps.setString(1, like);
        ps.setString(2, like);
    }

    public static void findActiveByName(PreparedStatement ps, String n) throws SQLException {
        ps.setString(1, n);
    }

    public static void insert(PreparedStatement ps, AiInsertParam e) throws SQLException {
        ps.setString(1, e.getQuestion());
        ps.setInt(2, e.getUsedChunks());
        ps.setString(3, e.getAnswer());
    }

    public static void findAllActive(PreparedStatement ps) throws SQLException {

    }    
}
