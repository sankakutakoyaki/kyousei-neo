package com.kyouseipro.neo.service.ks;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.ks.KsSalesEntity;
import com.kyouseipro.neo.repository.ks.KsSalesRepository;

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
    public List<KsSalesEntity> getBetweenKsSalesEntity(LocalDate start, LocalDate end, String type) {
        return ksSalesRepository.findByEntityFromBetween(start, end, type);
    }
}
