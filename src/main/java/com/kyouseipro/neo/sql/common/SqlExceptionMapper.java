package com.kyouseipro.neo.sql.common;

import java.sql.SQLException;

import com.kyouseipro.neo.common.exception.BusinessException;

public class SqlExceptionMapper {
    public static RuntimeException map(SQLException e){
        String message = e.getMessage();
        if(message != null){
            if(message.contains(
                    "UX_recycles_recycle_number_active")){
                return new BusinessException(
                    "お問合せ管理票番号が既に登録されています"
                );
            }
        }
        return new RuntimeException(
            "SQL実行エラー",
            e
        );
    }
}