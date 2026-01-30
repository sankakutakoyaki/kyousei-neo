package com.kyouseipro.neo.corporation.staff.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.corporation.staff.entity.StaffListEntity;
import com.kyouseipro.neo.corporation.staff.repository.StaffListRepository;

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
