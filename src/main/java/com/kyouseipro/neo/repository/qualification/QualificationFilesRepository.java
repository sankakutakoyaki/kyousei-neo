package com.kyouseipro.neo.repository.qualification;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.exception.BusinessException;
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
    public Optional<QualificationFilesEntity> findById(int id) {
        String sql = "SELECT * FROM qualifications_files WHERE NOT (state = ?) AND qualifications_files_id = ?";

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            rs -> rs.next() ? QualificationFilesEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 資格IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public List<QualificationFilesEntity> findByQualificationsId(int id) {
        String sql = "SELECT * FROM qualifications_files WHERE NOT (state = ?) AND qualifications_id = ?";

        return sqlRepository.findAll(
            sql,
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
        int count = sqlRepository.executeBatch(
            sql,
            (ps, en) -> QualificationFilesParameterBinder.bindInsert(ps, en, editor, id),
            list
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
    public int delete(String url, String editor) {
        String sql = QualificationFilesSqlBuilder.buildDelete();

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> QualificationFilesParameterBinder.bindDelete(ps, url, editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }
}

