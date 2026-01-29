package com.kyouseipro.neo.repository.qualification;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.qualification.QualificationsEntity;
import com.kyouseipro.neo.mapper.dto.SimpleDataMapper;
import com.kyouseipro.neo.mapper.qualification.QualificationsEntityMapper;
import com.kyouseipro.neo.query.parameter.qualification.QualificationsParameterBinder;
import com.kyouseipro.neo.query.sql.qualification.QualificationsSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;
import com.kyouseipro.neo.repository.personnel.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QualificationsRepository {

    private final SqlRepository sqlRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public List<QualificationsEntity> findAllByEmployeeId(int id) {
        String sql = QualificationsSqlBuilder.buildFindAllByEmployeeId();
        EmployeeEntity entity = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("従業員が見つかりません: " + id));

        int targetId = entity.getEmployeeId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> QualificationsParameterBinder.bindFindAllByEmployeeId(ps, targetId),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * IDによる許認可情報取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public List<QualificationsEntity> findAllByCompanyId(int id) {
        String sql = QualificationsSqlBuilder.buildFindAllByCompanyId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> QualificationsParameterBinder.bindFindAllByCompanyId(ps, id),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 取得状況全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<QualificationsEntity> findAllByEmployeeStatus() {
        String sql = QualificationsSqlBuilder.buildFindAllByEmployeeStatus();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> QualificationsParameterBinder.bindFindAllByEmployeeStatus(ps, null),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 取得状況全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<QualificationsEntity> findAllByCompanyStatus() {
        String sql = QualificationsSqlBuilder.buildFindAllByCompanyStatus();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> QualificationsParameterBinder.bindFindAllByCompanyStatus(ps, null),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * コンボボックス用リスト全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllByQualificationMasterCombo() {
        String sql = "SELECT qualification_master_id as number, name as text FROM qualification_master WHERE NOT (state = ?) ORDER BY code;";

        return sqlRepository.findAll(
            sql,
            (ps, v) ->  ps.setInt(1, Enums.state.DELETE.getCode()),
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllByLicenseMasterCombo() {
        String sql = "SELECT qualification_master_id as number, name as text FROM qualification_master WHERE NOT (state = ?) AND category_name = '許可' ORDER BY code;";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            SimpleDataMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(QualificationsEntity entity, String editor) {
        String sql = QualificationsSqlBuilder.buildInsert();

        return sqlRepository.executeRequired(
            sql,
            (ps, en) -> QualificationsParameterBinder.bindInsert(ps, en, editor),
            rs -> {
                if (!rs.next()) {
                    throw new BusinessException("登録に失敗しました");
                }
                int id = rs.getInt("qualifications_id");

                if (rs.next()) {
                    throw new IllegalStateException("ID取得結果が複数行です");
                }
                return id;
            },
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(QualificationsEntity entity, String editor) {
        String sql = QualificationsSqlBuilder.buildUpdate();

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> QualificationsParameterBinder.bindUpdate(ps, entity, editor)
        );

        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int delete(int id, String editor) {
        String sql = QualificationsSqlBuilder.buildDelete();

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> QualificationsParameterBinder.bindDelete(ps, id, editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(List<SimpleData> list, String editor) {
        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = QualificationsSqlBuilder.buildDeleteByIds(ids.size());

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> QualificationsParameterBinder.bindDeleteForIds(ps, ids, editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<QualificationsEntity> downloadCsvByIds(List<SimpleData> list, String editor) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = QualificationsSqlBuilder.buildDownloadCsvByIds(ids.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> QualificationsParameterBinder.bindDownloadCsvForIds(ps, ids),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }
}

