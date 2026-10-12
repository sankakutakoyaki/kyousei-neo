package com.kyouseipro.neo.domain.master.work.application;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.domain.master.work.model.WorkMasterDto;
import com.kyouseipro.neo.domain.master.work.model.WorkMasterEntity;
import com.kyouseipro.neo.domain.master.work.repository.WorkMasterRepository;

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