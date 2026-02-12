package com.kyouseipro.neo.corporation.staff.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.corporation.staff.entity.StaffListEntity;
import com.kyouseipro.neo.corporation.staff.mapper.StaffListEntityMapper;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StaffListRepository {
    private final SqlRepository sqlRepository;

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<StaffListEntity> findAll() {
        String sql = StaffSqlBuilder.buildFindAll();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            StaffListEntityMapper::map
        );
    }
    /**
     * 全件取得（営業担当）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<StaffListEntity> findBySalesStaff() {
        String sql = StaffSqlBuilder.buildFindByCategoryId();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.clientCategory.SHIPPER.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            StaffListEntityMapper::map
        );
    }
}
