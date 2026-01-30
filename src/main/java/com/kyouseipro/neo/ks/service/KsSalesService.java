package com.kyouseipro.neo.ks.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.ks.entity.KsSalesEntity;
import com.kyouseipro.neo.ks.repository.KsSalesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KsSalesService {
    private final KsSalesRepository ksSalesRepository;
    /**
     * 
     * @param start
     * @param end
     * @param col
     * @return
     */
    public List<KsSalesEntity> getAllFromBetween(LocalDate start, LocalDate end, String type) {
        return ksSalesRepository.findByAllFromBetween(start, end, type);
    }
}
