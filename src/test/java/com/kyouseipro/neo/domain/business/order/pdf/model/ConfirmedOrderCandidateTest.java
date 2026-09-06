package com.kyouseipro.neo.domain.business.order.pdf.model;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
class ConfirmedOrderCandidateTest {
    private Map<String,String> values() {
        return new HashMap<>(Map.of("customerName","確認済み氏名","address","確認済み住所","requestedDate","2026-09-06",
            "mobilePhone","090-0000-0000","contactNote","原本確認済み",
            "items","[{\"itemName\":\"エアコン\",\"itemModel\":\"ABC\",\"itemQuantity\":\"2\"}]",
            "works","[{\"orderWorkName\":\"リサイクル運搬\",\"orderWorkQuantity\":\"1\",\"orderWorkPrice\":\"\"}]"));
    }
    @Test void mapsConfirmedFieldsAndRowsWithoutInventingMasterCodes() {
        var c=ConfirmedOrderCandidate.parse(values(),new ObjectMapper());
        assertEquals("確認済み氏名",c.order().get("title"));
        assertEquals("確認済み住所",c.order().get("fullAddress"));
        assertEquals(java.sql.Date.valueOf("2026-09-06"),c.order().get("visitDate"));
        assertEquals(2,c.items().get(0).get("itemQuantity"));
        assertEquals("リサイクル運搬",c.works().get(0).get("orderWorkName"));
        assertEquals(0,c.works().get(0).get("orderWorkPrice"));
        assertFalse(c.works().get(0).containsKey("orderWorkCode"));
    }
    @Test void rejectsAmbiguousDateInvalidQuantityAndOversizedRemarks() {
        for (String date: new String[]{"9/6","9月6日","2026-02-30"}) {
            var v=values();v.put("requestedDate",date);
            assertThrows(BusinessException.class,()->ConfirmedOrderCandidate.parse(v,new ObjectMapper()));
        }
        var v=values();v.put("items","[{\"itemModel\":\"ABC\",\"itemQuantity\":\"\"}]");
        final var badQuantity=v;
        assertThrows(BusinessException.class,()->ConfirmedOrderCandidate.parse(badQuantity,new ObjectMapper()));
        v=values();v.put("contactNote","あ".repeat(256));final var tooLong=v;
        assertThrows(BusinessException.class,()->ConfirmedOrderCandidate.parse(tooLong,new ObjectMapper()));
    }
    @Test void permitsUndecidedDateAndNoItemsOrWorks() {
        var v=values();v.put("requestedDate","");v.put("items","[]");v.put("works","[]");
        var c=ConfirmedOrderCandidate.parse(v,new ObjectMapper());
        assertNull(c.order().get("visitDate"));assertTrue(c.items().isEmpty());assertTrue(c.works().isEmpty());
    }
}
