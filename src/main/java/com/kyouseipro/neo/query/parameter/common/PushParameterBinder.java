package com.kyouseipro.neo.query.parameter.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.data.SubscriptionRequest;

public class PushParameterBinder {

    public static int bindFindByEndpoint(PreparedStatement ps, String endpoint) throws SQLException {
        int index = 1;
        ps.setString(index++, endpoint);
        return index;
    }

    public static int bindInsertSubscription(PreparedStatement pstmt, SubscriptionRequest s, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, s.getEndpoint());
        pstmt.setString(index++, s.getP256dh());
        pstmt.setString(index++, s.getAuth());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindDeleteSubscriptionForEndpoint(PreparedStatement pstmt, String endpoint, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, endpoint);
        return index;
    }
}
