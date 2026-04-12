package com.kyouseipro.neo.common.master.combo;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.corporation.company.entity.CompanyEntity;
import com.kyouseipro.neo.domain.corporation.company.CompanyService;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComboService {
    private final CompanyService companyService;

    // public List<ComboDto> getPartnerCombo() {
    //     return toCombo(
    //         companyService.findComboByCategory(0),
    //         CompanyEntity::getCompanyId,
    //         CompanyEntity::getName
    //     );
    // }

    // public List<ComboDto> getEmployeeCombo() {
    //     return toCombo(
    //         employeeService.findAll(),
    //         EmployeeEntity::getEmployeeId,
    //         EmployeeEntity::getName
    //     );
    // }

    private <T> List<ComboDto> toCombo(
        List<T> list,
        Function<T, Long> valueMapper,
        Function<T, String> labelMapper
    ) {
        return list.stream()
            .map(e -> new ComboDto(
                valueMapper.apply(e),
                labelMapper.apply(e)
            ))
            .toList();
    }
}