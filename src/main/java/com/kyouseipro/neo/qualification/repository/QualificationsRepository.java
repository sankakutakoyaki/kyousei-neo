package com.kyouseipro.neo.qualification.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.common.simpledata.mapper.SimpleDataMapper;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.qualification.entity.QualificationsEntityRequest;
import com.kyouseipro.neo.qualification.entity.QualificationsEntity;
import com.kyouseipro.neo.qualification.mapper.QualificationsEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QualificationsRepository {

    private final SqlRepository sqlRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public QualificationsEntity findByQualificationsIdFromEmployee(int id) {
        String sql = QualificationsSqlBuilder.buildFindByQualificationsIdFromEmployee();
        return sqlRepository.queryOne(
            sql,
            (ps, i) ->{
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            QualificationsEntityMapper::map
        );
    }

    /**
     * IDによる許認可情報取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public QualificationsEntity findByQualificationsIdFromCompany(int id) {
        String sql = QualificationsSqlBuilder.buildFindByQualificationsIdFromCompany();
        return sqlRepository.queryOne(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            QualificationsEntityMapper::map
        );
    }
    
    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public List<QualificationsEntity> findAllByEmployeeId(int id) {
        String sql = QualificationsSqlBuilder.buildFindAllByEmployeeId();
        return sqlRepository.queryList(
            sql,
            (ps, i) ->{
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            QualificationsEntityMapper::map
        );
    }

    /**
     * IDによる許認可情報取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public List<QualificationsEntity> findAllByCompanyId(int id) {
        String sql = QualificationsSqlBuilder.buildFindAllByCompanyId();
        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            QualificationsEntityMapper::map
        );
    }

    /**
     * 取得済み全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<QualificationsEntity> findAllByEmployee() {
        String sql = QualificationsSqlBuilder.buildFindAllByEmployee();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            QualificationsEntityMapper::map
        );
    }

    /**
     * 取得済み全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<QualificationsEntity> findAllByCompany() {
        String sql = QualificationsSqlBuilder.buildFindAllByCompany();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            QualificationsEntityMapper::map
        );
    }

    /**
     * 取得状況全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<QualificationsEntity> findAllByEmployeeStatus() {
        String sql = QualificationsSqlBuilder.buildFindAllByEmployeeStatus();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            QualificationsEntityMapper::map
        );
    }

    /**
     * 取得状況全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<QualificationsEntity> findAllByCompanyStatus() {
        String sql = QualificationsSqlBuilder.buildFindAllByCompanyStatus();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            QualificationsEntityMapper::map
        );
    }

    /**
     * コンボボックス用リスト全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllByQualificationMasterCombo() {
        String sql = "SELECT qualification_master_id as number, name as text FROM qualification_master WHERE state <> ? ORDER BY category_name, name;";

        return sqlRepository.queryList(
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
        String sql = "SELECT qualification_master_id as number, name as text FROM qualification_master WHERE state <> ? AND category_name = '許可' ORDER BY category_name, name;";

        return sqlRepository.queryList(
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
    public int insert(QualificationsEntityRequest req, String editor) {
        String sql = QualificationsSqlBuilder.buildBulkInsert(req);

        return sqlRepository.insert(
            sql,
            (ps, en) -> QualificationsParameterBinder.bindBulkInsert(ps, en, editor),
            rs -> rs.getInt("qualifications_id"),
            req
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(QualificationsEntityRequest req, String editor) {
        String sql = QualificationsSqlBuilder.buildBulkUpdate(req);

        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> QualificationsParameterBinder.bindBulkUpdate(ps, e, editor),
            req
        );

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

        int count = sqlRepository.updateRequired(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
                ps.setString(index++, editor);
            }
        );

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

        int count = sqlRepository.updateRequired(
            sql,
            (ps, v) -> QualificationsParameterBinder.bindDeleteForIds(ps, ids, editor)
        );

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

        return sqlRepository.queryList(
            sql,
            (ps, e) -> QualificationsParameterBinder.bindDownloadCsvForIds(ps, e),
            QualificationsEntityMapper::map,
            ids
        );
    }
}

