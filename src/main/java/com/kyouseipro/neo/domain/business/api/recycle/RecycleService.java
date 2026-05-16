package com.kyouseipro.neo.domain.business.api.recycle;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.combo.entity.ComboDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecycleService {
    private final RecycleRepository recycleRepository;

        public List<ComboDto> findItemCombo() {
        return recycleRepository.findItemCombo();
    }
}
