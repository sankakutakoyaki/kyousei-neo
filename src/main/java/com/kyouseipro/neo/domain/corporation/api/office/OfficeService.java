package com.kyouseipro.neo.domain.corporation.api.office;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.combo.entity.ComboDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfficeService {
    private final OfficeRepository officeRepository;
    
    public List<ComboDto> findComboClientAll() {
        return officeRepository.findComboClientAll();
    }

    public List<ComboDto> findComboByCategory(int categoryCode) {
        return officeRepository.findComboByCategory(categoryCode);
    }
}
