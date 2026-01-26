package com.kyouseipro.neo.repository.personnel;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
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
            (ps, v) -> PaidHolidayParameterBinder.bindFindByOfficeIdFromYear(ps, id, year),
            PaidHolidayListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 年指定でIDで指定した従業員の有給リストを取得
     * @param year
     * @return
     */
    public List<PaidHolidayEntity> findByEmployeeIdFromYear(int id, String year) {
        String sql = PaidHolidaySqlBuilder.buildFindByEmployeeIdFromYear();
        // EmployeeEntity entity = employeeRepository.findById(id);

        EmployeeEntity entity = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("従業員が見つかりません: " + id));
        int targetId = entity.getEmployeeId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> PaidHolidayParameterBinder.bindFindByEmployeeIdFromYear(ps, targetId, year),
            PaidHolidayEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(PaidHolidayEntity entity, String editor) {
        String sql = PaidHolidaySqlBuilder.buildInsert();

        // // return sqlRepository.executeRequired(
        // //     sql,
        // //     (ps, en) -> PaidHolidayParameterBinder.bindInsert(ps, en, editor),
        // //     rs -> rs.next() ? rs.getInt("paid_holiday_id") : null,
        // //     p
        // // );
        // try {
            return sqlRepository.executeRequired(
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
        // } catch (RuntimeException e) {
        //     if (SqlExceptionUtil.isDuplicateKey(e)) {
        //         throw new BusinessException("このコードはすでに使用されています。");
        //     }
        //     throw e;
        // }
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int delete(int id, String editor) {
        String sql = PaidHolidaySqlBuilder.buildDelete();

        // Integer result = sqlRepository.executeUpdate(
        //     sql,
        //     ps -> PaidHolidayParameterBinder.bindDelete(ps, id, editor)
        // );

        // return result; // 成功件数。0なら削除なし
        int count = sqlRepository.executeUpdate(
            sql,
            ps -> PaidHolidayParameterBinder.bindDelete(ps, id, editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }
}
