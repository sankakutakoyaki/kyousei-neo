package com.kyouseipro.neo.corporation.company.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.corporation.company.entity.CompanyListEntity;
import com.kyouseipro.neo.corporation.company.repository.CompanyListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyListService {
    private final CompanyListRepository companyListRepository;

    
    /**
     * すべてのClientを取得
     * @return
     */
    public List<CompanyListEntity> getClientList() {
        return companyListRepository.findAllClient();
    }

    /**
     * すべてのCompanyを取得
     * @return
     */
    public List<CompanyListEntity> getList() {
        return companyListRepository.findAll();
    }

    /**
     * カテゴリー別のCompanyを取得
     * @return
     */
    public List<CompanyListEntity> getListByCategoryId(int category) {
        return companyListRepository.findByCategoryId(category);
    }
}
