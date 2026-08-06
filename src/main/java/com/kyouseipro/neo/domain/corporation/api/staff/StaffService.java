package com.kyouseipro.neo.domain.corporation.api.staff;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.combo.entity.ComboDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;
    
    public List<ComboDto> findComboAll() {
        return staffRepository.findComboAll();
    }
}
