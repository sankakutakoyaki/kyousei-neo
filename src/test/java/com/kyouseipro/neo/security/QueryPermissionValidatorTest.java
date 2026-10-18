package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;

class QueryPermissionValidatorTest {

    private final QueryPermissionValidator validator =
        new QueryPermissionValidator();

    @Test
    void saveWithTableMetaIsValid() {
        var def = new QueryDefinition(
            QueryType.UPDATE,
            QueryKind.SAVE,
            Tables.ORDER_BY_IDS
        );

        assertTrue(validator.validate(def).isEmpty());
    }

    @Test
    void customHandlerWithoutOperationIsInvalid() {
        var def = new QueryDefinition(
            QueryType.UPDATE,
            QueryKind.ORDER_SAVE,
            Tables.ORDER_BY_IDS
        );

        assertFalse(validator.validate(def).isEmpty());
    }

    @Test
    void customHandlerWithOperationIsValid() {
        var def = new QueryDefinition(
            QueryType.UPDATE,
            QueryKind.ORDER_SAVE,
            Tables.ORDER_BY_IDS
        );
        def.setPermissionOperation("order.save");

        assertTrue(validator.validate(def).isEmpty());
    }

    @Test
    void selectWithoutPermissionTableIsInvalid() {
        var def = QueryDefinition.select(
            "SELECT * FROM orders",
            List.of()
        );

        assertFalse(validator.validate(def).isEmpty());
    }

    @Test
    void selectWithPermissionTableIsValid() {
        var def = QueryDefinition.select(
            "SELECT * FROM orders",
            List.of()
        );
        def.setPermissionTable("orders");

        assertTrue(validator.validate(def).isEmpty());
    }

    @Test
    void csvWithoutPermissionTableIsInvalid() {
        var def = QueryDefinition.csv(
            "SELECT * FROM orders",
            List.of(),
            List.of()
        );

        assertFalse(validator.validate(def).isEmpty());
    }

    @Test
    void sqlUpdateWithoutOperationIsInvalid() {
        var def = QueryDefinition.update(
            "UPDATE orders SET remarks = ?",
            List.of("remarks")
        );

        assertFalse(validator.validate(def).isEmpty());
    }

    @Test
    void blankOperationIsInvalid() {
        var def = new QueryDefinition(
            QueryType.UPDATE,
            QueryKind.ORDER_SAVE,
            Tables.ORDER_BY_IDS
        );
        def.setPermissionOperation(" ");

        assertFalse(validator.validate(def).isEmpty());
    }

    @Test
    void recyclePriceListWithSelectPermissionIsValid() {
        var def = QueryDefinition.select(
            "SELECT * FROM recycle_prices",
            List.of()
        );
        def.setKind(QueryKind.RECYCLE_PRICE_LIST);
        def.setPermissionTable("recycle_prices");

        assertTrue(validator.validate(def).isEmpty());
    }

    @Test
    void recyclePriceDetailWithoutPermissionTableIsInvalid() {
        var def = QueryDefinition.select(
            "SELECT * FROM recycle_prices",
            List.of()
        );
        def.setKind(QueryKind.RECYCLE_PRICE_DETAIL);

        assertFalse(validator.validate(def).isEmpty());
    }

    @Test
    void recyclePriceListWithUpdateTypeIsInvalid() {
        var def = QueryDefinition.update(
            "UPDATE recycle_prices SET price = ?",
            List.of("price")
        );
        def.setKind(QueryKind.RECYCLE_PRICE_LIST);
        def.setPermissionTable("recycle_prices");

        assertTrue(
            validator.validate(def).stream()
                .anyMatch(message -> message.contains("QueryType.SELECT"))
        );
    }
}