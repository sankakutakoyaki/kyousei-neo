package com.kyouseipro.neo.service.corporation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.repository.corporation.StaffListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffListService {
    private final StaffListRepository staffListRepository;
    
    /**
     * すべてのStaffを取得
     * @return
     */
    public List<StaffListEntity> getList() {
        return staffListRepository.findAll();
    }
}
