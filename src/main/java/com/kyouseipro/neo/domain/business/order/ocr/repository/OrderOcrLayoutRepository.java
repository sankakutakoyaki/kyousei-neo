package com.kyouseipro.neo.domain.business.order.ocr.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.domain.business.order.ocr.model.OrderOcrLayout;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderOcrLayoutRepository {
    private final SqlRepository sqlRepository;

    public List<OrderOcrLayout> find(long primeConstractorId) {
        return sqlRepository.queryList("SELECT field_key, x, y, width, height FROM order_ocr_layouts WHERE prime_constractor_id = ?", (ps, ignored) -> ps.setLong(1, primeConstractorId), rs -> new OrderOcrLayout(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)), null);
    }

    public void save(long primeConstractorId, List<OrderOcrLayout> layouts) {
        for (OrderOcrLayout layout : layouts) {
            sqlRepository.update("MERGE order_ocr_layouts AS target USING (SELECT ? AS prime_constractor_id, ? AS field_key) AS source ON target.prime_constractor_id = source.prime_constractor_id AND target.field_key = source.field_key WHEN MATCHED THEN UPDATE SET x = ?, y = ?, width = ?, height = ?, update_date = SYSDATETIME() WHEN NOT MATCHED THEN INSERT (prime_constractor_id, field_key, x, y, width, height, update_date) VALUES (?, ?, ?, ?, ?, ?, SYSDATETIME());", List.of(primeConstractorId, layout.fieldKey(), layout.x(), layout.y(), layout.width(), layout.height(), primeConstractorId, layout.fieldKey(), layout.x(), layout.y(), layout.width(), layout.height()));
        }
    }
}
