package com.kyouseipro.neo.domain.management.timeworks.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.domain.management.timeworks.repository.TimeworkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeworkLocationService {

    private final TimeworkRepository repository;

    /**
     * GPS位置情報を勤怠打刻とは独立したトランザクションで保存する。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(
        long timeworkId, String stampType,
        double latitude, double longitude, double accuracy,
        java.time.LocalDateTime recordedAt, String editor
    ) {
        repository.insertLocation(
            timeworkId, stampType, latitude, longitude,
            accuracy, recordedAt, editor
        );
    }
}
