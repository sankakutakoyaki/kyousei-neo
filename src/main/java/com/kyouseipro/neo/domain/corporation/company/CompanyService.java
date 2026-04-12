package com.kyouseipro.neo.domain.corporation.company;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.master.combo.ComboDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public List<ComboDto> findComboAll() {
        return companyRepository.findComboAll();
    }

    public List<ComboDto> findComboClientAll() {
        return companyRepository.findComboClientAll();
    }

    public List<ComboDto> findComboByCategory(int categoryCode) {
        return companyRepository.findComboByCategory(categoryCode);
    }
}
