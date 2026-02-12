package com.kyouseipro.neo.ks.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.ks.entity.KsSalesEntity;
import com.kyouseipro.neo.ks.mapper.KsSalesStaffEntityMapper;

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
        
        return sqlRepository.queryList(
            sql,
            (ps, v) -> KsSalesParameterBinder.bindFindByBetween(ps, start, end),
            KsSalesStaffEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
