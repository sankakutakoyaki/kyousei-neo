package com.kyouseipro.neo.repository.personnel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.mapper.personnel.TimeworksListEntityMapper;
import com.kyouseipro.neo.query.parameter.personnel.TimeworksListParameterBinder;
import com.kyouseipro.neo.query.sql.personnel.TimeworksListSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TimeworksListRepository {
    private final SqlRepository sqlRepository;

    /**
     * IDによる取得。
     * @param employeeId
     * @return IDから取得した今日の日付のEntityをかえす。
     */
    public TimeworksListEntity findByTodaysEntityByEmployeeId(int employeeId) {
        String sql = TimeworksListSqlBuilder.buildFindByTodaysEntityByEmployeeId();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> TimeworksListParameterBinder.bindFindByTodaysEntityByEmployeeId(pstmt, comp),
            rs -> rs.next() ? TimeworksListEntityMapper.map(rs) : null,
            employeeId
        );
    }

    public List<TimeworksListEntity> findByDate(LocalDate date) {
        String sql = TimeworksListSqlBuilder.buildFindByDateSql();

        return sqlRepository.findAll(
            sql,
            ps -> TimeworksListParameterBinder.bindFindByDate(ps, date),
            TimeworksListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
