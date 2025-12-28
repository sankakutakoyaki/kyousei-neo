package com.kyouseipro.neo.repository.personnel;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.PaidHolidayEntity;
import com.kyouseipro.neo.entity.personnel.PaidHolidayListEntity;
import com.kyouseipro.neo.mapper.personnel.PaidHolidayEntityMapper;
import com.kyouseipro.neo.mapper.personnel.PaidHolidayListEntityMapper;
import com.kyouseipro.neo.query.parameter.personnel.PaidHolidayParameterBinder;
import com.kyouseipro.neo.query.sql.personnel.PaidHolidaySqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaidHolidayRepository {
    private final SqlRepository sqlRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * 年指定でIDで指定した営業所の勤怠情報を取得
     * @param year
     * @return
     */
    public List<PaidHolidayListEntity> findByOfficeIdFromYear(int id, String year) {
        String sql = PaidHolidaySqlBuilder.buildFindByOfficeIdFromYear();

        return sqlRepository.findAll(
            sql,
            ps -> PaidHolidayParameterBinder.bindFindByOfficeIdFromYear(ps, id, year),
            PaidHolidayListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 年指定でIDで指定した従業員の有給リストを取得
     * @param year
     * @return
     */
    public List<PaidHolidayEntity> findByEmployeeIdFromYear(int employeeId, String year) {
        String sql = PaidHolidaySqlBuilder.buildFindByEmployeeIdFromYear();
        EmployeeEntity entity = employeeRepository.findById(employeeId);
        int id = entity.getEmployee_id();

        return sqlRepository.findAll(
            sql,
            ps -> PaidHolidayParameterBinder.bindFindByEmployeeIdFromYear(ps, id, year),
            PaidHolidayEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * INSERT
     * @param t
     * @param editor
     * @return
     */
    public Integer insert(PaidHolidayEntity p, String editor) {
        String sql = PaidHolidaySqlBuilder.buildInsert();

        return sqlRepository.execute(
            sql,
            (pstmt, entity) -> PaidHolidayParameterBinder.bindInsert(pstmt, entity, editor),
            rs -> rs.next() ? rs.getInt("paid_holiday_id") : null,
            p
        );
    }

    /**
     * DELETE
     * @param w
     * @param editor
     * @return
     */
    public Integer delete(int id, String editor) {
        String sql = PaidHolidaySqlBuilder.buildDelete();

        Integer result = sqlRepository.executeUpdate(
            sql,
            ps -> PaidHolidayParameterBinder.bindDelete(ps, id, editor)
        );

        return result; // 成功件数。0なら削除なし
    }
}
