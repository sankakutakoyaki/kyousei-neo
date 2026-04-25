package com.kyouseipro.neo.domain.corporation.office;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.master.combo.ComboDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfficeService {
    private final OfficeRepository officeRepository;
    
    public List<ComboDto> findComboClientAll() {
        return officeRepository.findComboClientAll();
    }
}
