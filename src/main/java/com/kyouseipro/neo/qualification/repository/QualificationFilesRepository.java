package com.kyouseipro.neo.qualification.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.interfaces.FileUpload;
import com.kyouseipro.neo.qualification.entity.QualificationFilesEntity;
import com.kyouseipro.neo.qualification.mapper.QualificationFilesEntityMapper;

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
    public QualificationFilesEntity findById(int id) {
        return sqlRepository.queryOne(
            """
            SELECT * FROM qualifications_files WHERE NOT (state = ?) AND qualifications_files_id = ?
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            QualificationFilesEntityMapper::map
        );
    }

    /**
     * 資格IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public List<QualificationFilesEntity> findByQualificationsId(int id) {
        return sqlRepository.queryList(
            """
            SELECT * FROM qualifications_files WHERE NOT (state = ?) AND qualifications_id = ?
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            QualificationFilesEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(List<FileUpload> list, String editor, int id) {
        String sql = QualificationFilesSqlBuilder.buildInsert(list.size());
        int count = sqlRepository.batch(
            sql,
            (ps, en) -> QualificationFilesParameterBinder.bindInsert(ps, en, editor, id),
            list
        );
        return count;
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int delete(String url, String editor) {
        String sql = QualificationFilesSqlBuilder.buildDelete();

        int count = sqlRepository.update(
            sql,
            (ps, v) -> QualificationFilesParameterBinder.bindDelete(ps, url, editor)
        );

        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }
}

