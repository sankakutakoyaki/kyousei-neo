package com.kyouseipro.neo.service.common;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.repository.common.ValidateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateService {
    private final ValidateRepository repository;

    public void validateCodeAbbrRule(int code, String abbrName) {

        // 除外コード
        if (code == 0 || code == 999) return;

        Optional<String> existing = repository.findAbbrNameByCode(code);

        if (existing.isPresent() && !existing.get().equals(abbrName)) {
            throw new BusinessException(
                "同じコードに異なる略称は登録できません。"
            );
        }
    }
}
