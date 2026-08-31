package com.kyouseipro.neo.domain.business.recycle.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.domain.business.recycle.repository.RecycleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecycleService {
    private final RecycleRepository recycleRepository;

    public List<ComboDto> findItemCombo() {
        return recycleRepository.findItemCombo();
    }

    public List<ComboDto> findMakerCombo() {
        return recycleRepository.findMakerCombo();
    }
}
