package com.kyouseipro.neo.personnel.workingconditions.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.personnel.workingconditions.entity.WorkingConditionsListEntity;
import com.kyouseipro.neo.personnel.workingconditions.repository.WorkingConditionsListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkingConditionsListService {
    private final WorkingConditionsListRepository workingConditionsListRepository;

    /**
     * すべてのWorkingConditionsを取得
     * @return
     */
    public List<WorkingConditionsListEntity> getList() {
        return workingConditionsListRepository.findAll();
    }

    /**
     * カテゴリー別のWorkingConditionsを取得
     * @return
     */
    public List<WorkingConditionsListEntity> getListByCategoryId(int category) {
        return workingConditionsListRepository.findByCategoryId(category);
    }    
}
