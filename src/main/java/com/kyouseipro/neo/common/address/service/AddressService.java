package com.kyouseipro.neo.common.address.service;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.address.entity.AddressDto;
import com.kyouseipro.neo.common.address.entity.AddressEntity;
import com.kyouseipro.neo.common.address.repository.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressDto findByPostalCode(String postalCode) {

        // ① 数字だけにする
        String digits = postalCode.replaceAll("[^0-9]", "");

        // ② ハイフン形式にする
        String formatted = formatPostalCode(digits);

        // ★ 検索はどっちでもOK（DBに合わせる）
        AddressEntity e = addressRepository.findByPostalCode(formatted);
        // もしDBがハイフン付きなら formatted を使う

        if (e == null) return null;

        return new AddressDto(
            formatted,              // ★ ハイフンありで返す
            buildAddress(e)
        );
    }

    private String buildAddress(AddressEntity e){
        return safe(e.getPrefecture())
             + safe(e.getCity())
             + safe(e.getTown());
    }

    private String safe(String s){
        return s != null ? s : "";
    }

    private String formatPostalCode(String digits) {
        if (digits == null || digits.length() != 7) return digits;

        return digits.substring(0, 3) + "-" + digits.substring(3);
    }
}