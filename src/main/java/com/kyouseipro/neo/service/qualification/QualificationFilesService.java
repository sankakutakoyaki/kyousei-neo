package com.kyouseipro.neo.service.qualification;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.qualification.QualificationFilesEntity;
import com.kyouseipro.neo.interfaceis.FileUpload;
import com.kyouseipro.neo.repository.qualification.QualificationFilesRepository;

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
    public List<QualificationFilesEntity> getQualificationFilesById(Integer id, String userName) {
        return qualificationFilesRepository.findByQualificationsId(id);
    }

    /**
     * URLからQualificationsFilesを削除
     * @param ids
     * @return
     */
    public Integer deleteQualificationsFilesByUrl(String url, String userName) {
        return qualificationFilesRepository.delete(url, userName);
    }

    /**
     * QualificationsFilesを登録します。
     * @param entity
     * @param editor
     * @return
    */
    public Integer saveQualificationsFiles(List<FileUpload> entities, String editor, Integer id) {
        return qualificationFilesRepository.insert(entities, editor, id);
    }
}
