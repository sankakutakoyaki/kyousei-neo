package com.kyouseipro.neo.service.personnel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;
import com.kyouseipro.neo.repository.personnel.EmployeeListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeListService {
    private final EmployeeListRepository employeeListRepository;

    /**
     * すべてのEmployeeを取得
     * @return
     */
    public List<EmployeeListEntity> getList() {
        return employeeListRepository.findAll();
    }

    /**
     * カテゴリー別のEmployeeを取得
     * @return
     */
    public List<EmployeeListEntity> getListByCategoryId(int category) {
        return employeeListRepository.findByCategoryId(category);
    }
}
