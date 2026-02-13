package com.kyouseipro.neo.common.validation.service;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.validation.repository.ValidateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateService {
    private final ValidateRepository repository;

    public void validateCodeAbbrRule(int code, String abbrName) {

        // 除外コード
        if (code == 0 || code == 999) return;

        String existing = repository.findAbbrNameByCode(code);

        if (existing != null && !existing.equals(abbrName)) {
            throw new BusinessException(
                "同じコードに異なる略称は登録できません。"
            );
        }
    }
}
