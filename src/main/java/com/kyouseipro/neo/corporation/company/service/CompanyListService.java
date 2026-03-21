package com.kyouseipro.neo.corporation.company.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.corporation.company.dto.CompanyListResponse;
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
    public List<CompanyListResponse> getClientList() {
        return companyListRepository.findAllClient();
    }

        
    /**
     * すべてのPartnerを取得
     * @return
     */
    public List<CompanyListResponse> getPartnerList() {
        return companyListRepository.findAllPartner();
    }

    /**
     * すべてのCompanyを取得
     * @return
     */
    public List<CompanyListResponse> getList() {
        return companyListRepository.findAll();
    }

    /**
     * カテゴリー別のCompanyを取得
     * @return
     */
    public List<CompanyListResponse> getListByCategoryId(int category) {
        return companyListRepository.findByCategoryId(category);
    }
}
