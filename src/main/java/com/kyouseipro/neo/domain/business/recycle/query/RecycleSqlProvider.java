package com.kyouseipro.neo.domain.business.recycle.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.business.recycle.RecycleItemQuery;
import com.kyouseipro.neo.sql.query.business.recycle.RecycleMakerQuery;
import com.kyouseipro.neo.sql.query.business.recycle.RecycleManufacturerQuery;
import com.kyouseipro.neo.sql.query.business.recycle.RecyclePriceQuery;
import com.kyouseipro.neo.sql.query.business.recycle.RecycleQuery;

@Component
public class RecycleSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        // ===== Recycle =====
        map.put(QueryId.RECYCLE_DETAIL, RecycleQuery.recycleDetail());
        map.put(QueryId.RECYCLE_CSV, RecycleQuery.recycleCsv());
        map.put(QueryId.RECYCLE_DELETE_BY_IDS, new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.RECYCLE_BY_IDS));
        map.put(QueryId.RECYCLE_SAVE, new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.RECYCLE_BY_IDS));
        map.put(QueryId.RECYCLE_DELIVERY_SAVE, new QueryDefinition(QueryType.UPDATE, QueryKind.RECYCLE_DELIVERY_SAVE, Tables.RECYCLE_BY_IDS));
        map.put(QueryId.RECYCLE_SHIPPING_SAVE, new QueryDefinition(QueryType.UPDATE, QueryKind.RECYCLE_SHIPPING_SAVE, Tables.RECYCLE_BY_IDS));
        map.put(QueryId.RECYCLE_LOSS_SAVE, new QueryDefinition(QueryType.UPDATE, QueryKind.RECYCLE_LOSS_SAVE, Tables.RECYCLE_BY_IDS));
        // ===== RecycleMaker =====
        map.put(QueryId.RECYCLE_MAKER_DETAIL, RecycleMakerQuery.recycleMakerDetail());
        map.put(QueryId.RECYCLE_MAKER_LIST, RecycleMakerQuery.recycleMakerList());
        map.put(QueryId.RECYCLE_MAKER_CSV, RecycleMakerQuery.recycleMakerCsv());
        map.put(QueryId.RECYCLE_MAKER_DELETE_BY_IDS, new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.RECYCLE_MAKER_BY_IDS));
        map.put(QueryId.RECYCLE_MAKER_SAVE, new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.RECYCLE_MAKER_BY_IDS));
        // ===== RecycleManufacturer =====
        map.put(QueryId.RECYCLE_MANUFACTURER_DETAIL, RecycleManufacturerQuery.recycleManufacturerDetail());
        map.put(QueryId.RECYCLE_MANUFACTURER_LIST, RecycleManufacturerQuery.recycleManufacturerList());
        map.put(QueryId.RECYCLE_MANUFACTURER_CSV, RecycleManufacturerQuery.recycleManufacturerCsv());
        map.put(QueryId.RECYCLE_MANUFACTURER_DELETE_BY_IDS, new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.RECYCLE_MANUFACTURER_BY_IDS));
        map.put(QueryId.RECYCLE_MANUFACTURER_SAVE, new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.RECYCLE_MANUFACTURER_BY_IDS));
        // ===== RecyclePrice =====
        map.put(QueryId.RECYCLE_PRICE_DETAIL, RecyclePriceQuery.recyclePriceDetail());
        map.put(QueryId.RECYCLE_PRICE_LIST, RecyclePriceQuery.recyclePriceList());
        map.put(QueryId.RECYCLE_PRICE_CSV, RecyclePriceQuery.recyclePriceCsv());
        map.put(QueryId.RECYCLE_PRICE_DELETE_BY_IDS, new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.RECYCLE_PRICE_BY_IDS));
        map.put(QueryId.RECYCLE_PRICE_SAVE, new QueryDefinition(QueryType.UPDATE, QueryKind.RECYCLE_PRICE_SAVE, Tables.RECYCLE_PRICE_BY_IDS));
        // ===== RecycleItem =====
        map.put(QueryId.RECYCLE_ITEM_LIST, RecycleItemQuery.recycleItemList());
        // ===== 参照権限 =====
        map.get(QueryId.RECYCLE_DETAIL).setPermissionTable(Tables.RECYCLE_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_CSV).setPermissionTable(Tables.RECYCLE_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_MAKER_DETAIL).setPermissionTable(Tables.RECYCLE_MAKER_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_MAKER_LIST).setPermissionTable(Tables.RECYCLE_MAKER_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_MAKER_CSV).setPermissionTable(Tables.RECYCLE_MAKER_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_MANUFACTURER_DETAIL).setPermissionTable(Tables.RECYCLE_MANUFACTURER_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_MANUFACTURER_LIST).setPermissionTable(Tables.RECYCLE_MANUFACTURER_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_MANUFACTURER_CSV).setPermissionTable(Tables.RECYCLE_MANUFACTURER_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_PRICE_CSV).setPermissionTable(Tables.RECYCLE_PRICE_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_ITEM_LIST).setPermissionTable("recycle_items");
        map.get(QueryId.RECYCLE_PRICE_LIST).setPermissionTable(Tables.RECYCLE_PRICE_BY_IDS.tableName());
        map.get(QueryId.RECYCLE_PRICE_DETAIL).setPermissionTable(Tables.RECYCLE_PRICE_BY_IDS.tableName());
        // ===== 業務操作権限 =====
        map.get(QueryId.RECYCLE_DELIVERY_SAVE).setPermissionOperation("recycle.delivery.save");
        map.get(QueryId.RECYCLE_SHIPPING_SAVE).setPermissionOperation("recycle.shipping.save");
        map.get(QueryId.RECYCLE_LOSS_SAVE).setPermissionOperation("recycle.loss.save");
        map.get(QueryId.RECYCLE_PRICE_SAVE).setPermissionOperation("recycle.price.save");
        // ===== 料金表参照権限 =====
        map.get(QueryId.RECYCLE_PRICE_LIST).setPermissionTable(Tables.RECYCLE_PRICE_BY_IDS.tableName());

        return map;
    }
}