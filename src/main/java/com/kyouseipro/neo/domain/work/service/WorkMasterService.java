package com.kyouseipro.neo.domain.work.service;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.domain.work.entity.WorkMasterDto;
import com.kyouseipro.neo.domain.work.entity.WorkMasterEntity;
import com.kyouseipro.neo.domain.work.repository.WorkMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkMasterService {

    private final WorkMasterRepository workMasterRepository;

    public WorkMasterDto findByWorkCode(String workCode) {

        WorkMasterEntity e = workMasterRepository.findByWorkCode(workCode);

        if (e == null) {
            return null;
        }

        return new WorkMasterDto(
            e.getWorkCode(),
            e.getWorkName(),
            e.getWorkPrice()
        );
    }
}