package com.kyouseipro.neo.corporation.office.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.corporation.office.entity.OfficeListEntity;
import com.kyouseipro.neo.corporation.office.repository.OfficeListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfficeListService {
    private final OfficeListRepository officeListRepository;

    /**
     * すべてのOfficeを取得
     * @return
     */
    public List<OfficeListEntity> getList() {
        return officeListRepository.findAll();
    }

    /**
     * すべてのClientOfficeを取得
     * @return
     */
    public List<OfficeListEntity> getClientList() {
        return officeListRepository.findByCategoryId(0);
    }

    /**
     * カテゴリー別のOfficeを取得
     * @return
     */
    public List<OfficeListEntity> getListByCategoryId(int category) {
        return officeListRepository.findByCategoryId(category);
    } 
}
