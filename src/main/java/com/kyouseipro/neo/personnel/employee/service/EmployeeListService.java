package com.kyouseipro.neo.personnel.employee.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.personnel.employee.entity.EmployeeListEntity;
import com.kyouseipro.neo.personnel.employee.repository.EmployeeListRepository;

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
