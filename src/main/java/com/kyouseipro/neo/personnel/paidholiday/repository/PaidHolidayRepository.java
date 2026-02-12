package com.kyouseipro.neo.personnel.paidholiday.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.repository.EmployeeRepository;
import com.kyouseipro.neo.personnel.paidholiday.entity.PaidHolidayEntity;
import com.kyouseipro.neo.personnel.paidholiday.entity.PaidHolidayListEntity;
import com.kyouseipro.neo.personnel.paidholiday.mapper.PaidHolidayEntityMapper;
import com.kyouseipro.neo.personnel.paidholiday.mapper.PaidHolidayListEntityMapper;

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

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setString(index++, year);
                ps.setString(index++, year);        
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            PaidHolidayListEntityMapper::map
        );
    }

    /**
     * 年指定でIDで指定した従業員の有給リストを取得
     * @param year
     * @return
     */
    public List<PaidHolidayEntity> findByEmployeeIdFromYear(int id, String year) {
        String sql = PaidHolidaySqlBuilder.buildFindByEmployeeIdFromYear();

        EmployeeEntity entity = employeeRepository.findById(id);
            // .orElseThrow(() -> new RuntimeException("従業員が見つかりません: " + id));
        int targetId = entity.getEmployeeId();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, targetId);
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setString(index++, year);
                ps.setString(index++, year);
            },
            PaidHolidayEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(PaidHolidayEntity entity, String editor) {
        String sql = PaidHolidaySqlBuilder.buildInsert();

        return sqlRepository.queryOne(
            sql,
            (ps, en) -> PaidHolidayParameterBinder.bindInsert(ps, en, editor),
            rs -> {
                if (!rs.next()) {
                    throw new BusinessException("登録に失敗しました");
                }
                int id = rs.getInt("paid_holiday_id");

                if (rs.next()) {
                    throw new IllegalStateException("ID取得結果が複数行です");
                }
                return id;
            },
            entity
        );
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int delete(int id, String editor) {
        String sql = PaidHolidaySqlBuilder.buildDelete();

        int count = sqlRepository.update(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
                ps.setString(index++, editor);
            }
        );
        
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }
}
