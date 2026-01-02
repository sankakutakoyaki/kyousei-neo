package com.kyouseipro.neo.repository.qualification;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.entity.qualification.QualificationFilesEntity;
import com.kyouseipro.neo.interfaceis.FileUpload;
import com.kyouseipro.neo.mapper.qualification.QualificationFilesEntityMapper;
import com.kyouseipro.neo.query.parameter.qualification.QualificationFilesParameterBinder;
import com.kyouseipro.neo.query.sql.qualification.QualificationFilesSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QualificationFilesRepository {
    private final SqlRepository sqlRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<QualificationFilesEntity> findById(Integer id) {
        String sql = QualificationFilesSqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> QualificationFilesParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? QualificationFilesEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 資格IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public List<QualificationFilesEntity> findByQualificationsId(Integer id) {
        String sql = QualificationFilesSqlBuilder.buildFindAllByQualificationsId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> QualificationFilesParameterBinder.bindFindAllByQualificationsId(ps, id),
            QualificationFilesEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(List<FileUpload> list, String editor, Integer id) {
        String sql = QualificationFilesSqlBuilder.buildInsert(list.size());
        // return sqlRepository.executeUpdate(
        //     sql,
        //     ps -> QualificationFilesParameterBinder.bindInsert(ps, entities, editor, id)
        // );
        // String sql = TimeworksListSqlBuilder.buildUpdate(); // UPDATE ... WHERE id = ?

        // return sqlRepository.executeBatch(
        //     sql,
        //     (ps, en) -> TimeworksListParameterBinder.bindUpdate(ps, en, editor),
        //     list
        // );
        try {
            int count = sqlRepository.executeBatch(
                sql,
                (ps, en) -> QualificationFilesParameterBinder.bindInsert(ps, en, editor, id),
                list
            );

            if (count == 0) {
                throw new BusinessException("更新対象が存在しません");
            }

            return count;

        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer delete(String url, String editor) {
        String sql = QualificationFilesSqlBuilder.buildDelete();

        // return sqlRepository.executeUpdate(
        //     sql,
        //     ps -> QualificationFilesParameterBinder.bindDelete(ps, url, editor)
        // );
        int count = sqlRepository.executeUpdate(
            sql,
            ps -> QualificationFilesParameterBinder.bindDelete(ps, url, editor)
        );
        if (count == 0) {
            throw new BusinessException("削除対象が存在しません");
        }

        return count;
    }
}

