package com.kyouseipro.neo.domain.personnel.employee;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.combo.entity.ComboDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeWorkCategoryService {

    private final EmployeeWorkCategoryRepository repository;

    public List<ComboDto> findCombo() {
        return repository.findCombo();
    }
}