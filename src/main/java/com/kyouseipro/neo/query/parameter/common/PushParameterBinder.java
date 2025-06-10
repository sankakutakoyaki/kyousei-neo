package com.kyouseipro.neo.query.parameter.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.data.SubscriptionRequest;

public class PushParameterBinder {

    public static void bindFindByEndpoint(PreparedStatement ps, String endpoint) throws SQLException {
        ps.setString(1, endpoint);
    }

    public static void bindInsertSubscriptionParameters(PreparedStatement pstmt, SubscriptionRequest s, String editor) throws SQLException {
        pstmt.setString(1, s.getEndpoint());
        pstmt.setString(2, s.getP256dh());
        pstmt.setString(3, s.getAuth());

        pstmt.setString(4, editor);
    }

    public static void bindDeleteSubscriptionForEndpoint(PreparedStatement pstmt, String endpoint, String editor) throws SQLException {
        pstmt.setString(1, endpoint);
    }
}
