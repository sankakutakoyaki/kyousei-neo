package com.kyouseipro.neo.domain.business.recycle.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;
import com.kyouseipro.neo.sql.model.SqlResult;
import com.kyouseipro.neo.sql.model.TableMeta;

public class RecycleSqlBuilder {
        
    public static SqlResult buildRecycleDeliverySave(
            TableMeta meta,
            Map<String,Object> req,
            LogSqlProvider logProvider
    ) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        String tableVar = "@UpdatedRows";
        sql.append(logProvider.buildLogTable(tableVar));
        sql.append("""
            UPDATE recycles
            SET delivery_date = ?, version = version + 1, update_date = SYSDATETIME()
        """);

        params.add(req.get("deliveryDate"));
        sql.append(logProvider.buildOutput()).append(" INTO ").append(tableVar).append(" ");
        sql.append("""
            WHERE recycle_number = ?
            AND use_date IS NOT NULL
            AND delivery_date IS NULL
            AND NOT(state = ?);
        """);
        params.add(req.get("recycleNumber"));
        params.add(State.DELETE.getCode());
        String action = "UPDATE";
        sql.append(logProvider.buildInsertLog(tableVar, action));

        params.addAll(logProvider.buildLogParams(req, action));

        sql.append("""
            SELECT COUNT(*) FROM %s;
        """.formatted(tableVar));
        return new SqlResult(sql.toString(), params);
    }

    public static SqlResult buildRecycleShippingSave(
            TableMeta meta,
            Map<String,Object> req,
            LogSqlProvider logProvider
    ) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        String tableVar = "@UpdatedRows";
        sql.append(logProvider.buildLogTable(tableVar));
        sql.append("""
            UPDATE recycles
            SET shipping_date = ?, version = version + 1, update_date = SYSDATETIME()
        """);

        params.add(req.get("shippingDate"));
        sql.append(logProvider.buildOutput()).append(" INTO ").append(tableVar).append(" ");
        sql.append("""
            WHERE recycle_number = ?
            AND use_date IS NOT NULL
            AND delivery_date IS NOT NULL
            AND shipping_date IS NULL
            AND NOT(state = ?);
        """);
        params.add(req.get("recycleNumber"));
        params.add(State.DELETE.getCode());
        String action = "UPDATE";
        sql.append(logProvider.buildInsertLog(tableVar, action));

        params.addAll(logProvider.buildLogParams(req, action));

        sql.append("""
            SELECT COUNT(*) FROM %s;
        """.formatted(tableVar));
        return new SqlResult(sql.toString(), params);
    }

    public static SqlResult buildRecycleLossUpdate(
            TableMeta meta,
            Map<String,Object> req,
            LogSqlProvider logProvider
    ) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        String tableVar = "@UpdatedRows";
        sql.append(logProvider.buildLogTable(tableVar));
        sql.append("""
            UPDATE recycles
            SET
                company_id = 0,
                office_id = 0,
                maker_id = 0,
                item_id = 0,

                use_date = NULL,
                delivery_date = NULL,
                shipping_date = NULL,

                loss_date = ?,
                remarks = ?,

                version = version + 1,
                update_date = SYSDATETIME()
        """);

        params.add(req.get("lossDate"));
        params.add(req.get("remarks"));

        sql.append(logProvider.buildOutput()).append(" INTO ").append(tableVar).append(" ");
        sql.append("""
            WHERE recycle_number = ?
            AND NOT(state = ?);
        """);

        params.add(req.get("recycleNumber"));
        params.add(State.DELETE.getCode());

        String action = "UPDATE";
        sql.append(logProvider.buildInsertLog(tableVar, action));
        params.addAll(logProvider.buildLogParams(req, action));
        sql.append("""
            SELECT COUNT(*) FROM %s;
        """.formatted(tableVar));

        return new SqlResult(sql.toString(), params);
    }

    public static SqlResult buildRecycleLossInsert(
            TableMeta meta,
            Map<String,Object> req,
            LogSqlProvider logProvider
    ) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        String tableVar = "@InsertedRows";
        sql.append(logProvider.buildLogTable(tableVar));
        sql.append("""
            INSERT INTO recycles (
                recycle_number,
                loss_date,
                remarks,
                state
            )
        """);

        sql.append(logProvider.buildOutput()).append(" INTO ").append(tableVar).append(" ");
        sql.append("""
            VALUES (?, ?, ?, ?);
        """);
        params.add(req.get("recycleNumber"));
        params.add(req.get("lossDate"));
        params.add(req.get("remarks"));
        params.add(State.INITIAL.getCode());

        String action = "INSERT";
        sql.append(logProvider.buildInsertLog(tableVar, action));
        params.addAll(logProvider.buildLogParams(req, action));
        sql.append("""
            SELECT COUNT(*) FROM %s;
        """.formatted(tableVar));

        return new SqlResult(sql.toString(), params);
    }
}
