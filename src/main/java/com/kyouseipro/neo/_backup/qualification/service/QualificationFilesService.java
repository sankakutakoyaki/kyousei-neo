package com.kyouseipro.neo._backup.qualification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo._backup.FileUpload;
import com.kyouseipro.neo._backup.Enums.HistoryTables;
import com.kyouseipro.neo._backup.qualification.entity.QualificationFilesEntity;
import com.kyouseipro.neo._backup.qualification.repository.QualificationFilesRepository;
import com.kyouseipro.neo.interfaces.HistoryTarget;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QualificationFilesService {
    private final QualificationFilesRepository  qualificationFilesRepository;
    
    /**
     * 資格IDからPFD情報を取得
     * @param id
     * @param userName
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.QUALIFICATIONS,
        action = "削除"
    )
    public List<QualificationFilesEntity> getQualificationFilesById(Integer id, String userName) {
        return qualificationFilesRepository.findByQualificationsId(id);
    }

    /**
     * URLからQualificationsFilesを削除
     * @param ids
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.QUALIFICATIONS,
        action = "削除"
    )
    public Integer deleteQualificationsFilesByUrl(String url, String userName) {
        return qualificationFilesRepository.delete(url, userName);
    }

    /**
     * QualificationsFilesを登録します。
     * @param entity
     * @param editor
     * @return
    */
    @HistoryTarget(
        table = HistoryTables.QUALIFICATIONS,
        action = "登録"
    )
    public Integer saveQualificationsFiles(List<FileUpload> entities, String editor, Integer id) {
        return qualificationFilesRepository.insert(entities, editor, id);
    }
}
