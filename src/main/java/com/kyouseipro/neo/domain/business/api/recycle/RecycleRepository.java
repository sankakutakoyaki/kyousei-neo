package com.kyouseipro.neo.domain.business.api.recycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;
import com.kyouseipro.neo.sql.model.SqlResult;
import com.kyouseipro.neo.sql.model.TableMeta;
import com.kyouseipro.neo.sql.provider.LogSqlProviderResolver;
import com.kyouseipro.neo.sql.query.recycle.RecycleSqlBuilder;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecycleRepository {
    private final SqlRepository sqlRepository;
    private final LogSqlProviderResolver resolver;

    public List<ComboDto> findItemCombo() {
        String sql = """
            SELECT *
            FROM recycle_items
            WHERE state = ? ORDER BY code
        """;

        return sqlRepository.queryList(
            sql,
            (ps, p) -> {
                ps.setInt(1, State.INITIAL.getCode());
            },
            rs -> {
                ComboDto c = new ComboDto(
                    rs.getLong("code"),
                    rs.getString("name"),
                    rs.getLong("recycle_item_id"));
                return c;
            }
        );
    }

    public int updateRecycleDelivery(
            TableMeta meta,
            Map<String,Object> req,
            String editor
    ){
        req.put("editor", editor);

        String recycleNumber = String.valueOf(req.get("recycleNumber"));
        Map<String,Object> recycle = findRecycleByNumber(recycleNumber);
        if(recycle == null){
            throw new BusinessException("使用登録されていないため引渡登録できません");
        }
        if(recycle.get("shippingDate") != null){
            throw new BusinessException("既に発送登録済です");
        }

        LogSqlProvider logProvider = resolver.resolve(meta.tableName());
        SqlResult result = RecycleSqlBuilder.buildRecycleDeliverySave(
            meta,
            req,
            logProvider
        );
        return sqlRepository.updateRequired(
            result.getSql(),
            result.getParams(),
            "引渡登録に失敗しました"
        );
    }

    public int updateRecycleShipping(
            TableMeta meta,
            Map<String,Object> req,
            String editor
    ){
        req.put("editor", editor);

        String recycleNumber = String.valueOf(req.get("recycleNumber"));
        Map<String,Object> recycle = findRecycleByNumber(recycleNumber);
        if(recycle == null){
            throw new BusinessException("使用登録されていないため発送登録できません");
        }
        if(recycle.get("deliveryDate") == null){
            throw new BusinessException("引渡されていないため発送登録できません");
        }
        if(recycle.get("shippingDate") != null){
            throw new BusinessException("既に発送登録済です");
        }

        LogSqlProvider logProvider = resolver.resolve(meta.tableName());
        SqlResult result = RecycleSqlBuilder.buildRecycleShippingSave(
            meta,
            req,
            logProvider
        );
        return sqlRepository.updateRequired(
            result.getSql(),
            result.getParams(),
            "発送登録に失敗しました"
        );
    }

    public int updateRecycleLoss(
            TableMeta meta,
            Map<String,Object> req,
            String editor
    ){
        req.put("editor", editor);
        String recycleNumber = String.valueOf(req.get("recycleNumber"));
        Map<String,Object> recycle = findRecycleByNumber(recycleNumber);

        if(recycle == null){
            return insertLoss(meta, req, editor);
        }
        return updateLoss(meta, req, editor);
    }

    private Map<String,Object> findRecycleByNumber(String recycleNumber){
        String sql = """
            SELECT
                recycle_id,
                recycle_number,
                use_date,
                delivery_date,
                shipping_date,
                loss_date,
                state
            FROM recycles
            WHERE recycle_number = ?
            AND state = ?
        """;
        List<Map<String,Object>> list = 
            sqlRepository.selectMap(sql, List.of(recycleNumber, State.INITIAL.getCode()));
        return list.isEmpty()? null: list.get(0);
    }

    private int updateLoss(
            TableMeta meta,
            Map<String,Object> req,
            String editor
    ){
        LogSqlProvider logProvider = resolver.resolve(meta.tableName());
        SqlResult result = RecycleSqlBuilder.buildRecycleLossUpdate(meta, req, logProvider);

        return sqlRepository.updateRequired(
            result.getSql(),
            result.getParams(),
            "ロス登録に失敗しました"
        );
    }

    private int insertLoss(
            TableMeta meta,
            Map<String,Object> req,
            String editor
    ){
        Map<String,Object> insert = new HashMap<>();
        insert.put("recycleNumber", req.get("recycleNumber"));
        insert.put("lossDate", req.get("lossDate"));
        insert.put("remarks", req.get("remarks"));
        insert.put("state", State.INITIAL.getCode());
        insert.put("editor", editor);

        LogSqlProvider logProvider = resolver.resolve(meta.tableName());
        SqlResult result = RecycleSqlBuilder.buildRecycleLossInsert(meta, insert, logProvider);

        return sqlRepository.updateRequired(
            result.getSql(),
            result.getParams(),
            "ロス登録に失敗しました"
        );
    }
}
