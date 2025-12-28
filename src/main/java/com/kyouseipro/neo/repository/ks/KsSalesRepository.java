package com.kyouseipro.neo.repository.ks;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.ks.KsSalesEntity;
import com.kyouseipro.neo.mapper.ks.KsSalesStaffEntityMapper;
import com.kyouseipro.neo.query.parameter.ks.KsSalesParameterBinder;
import com.kyouseipro.neo.query.sql.ks.KsSalesSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class KsSalesRepository {
    private final SqlRepository sqlRepository;

    /**
     * 期間内のアイテムリストを取得
     * @param start
     * @param end
     * @return
     */
    public List<KsSalesEntity> findByAllFromBetween(LocalDate start, LocalDate end, String type) {
        String sql = "";
        switch (type) {
            case "staff":
                sql = KsSalesSqlBuilder.buildFindByBetweenByStaff();
                break;
        
            default:
                break;
        }
        
        return sqlRepository.findAll(
            sql,
            ps -> KsSalesParameterBinder.bindFindByBetween(ps, start, end),
            KsSalesStaffEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
