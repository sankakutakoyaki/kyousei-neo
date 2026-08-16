package com.kyouseipro.neo.domain.item.service;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.domain.item.entity.ItemMasterDto;
import com.kyouseipro.neo.domain.item.entity.ItemMasterEntity;
import com.kyouseipro.neo.domain.item.repository.ItemMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemMasterService {

    private final ItemMasterRepository itemMasterRepository;

    public ItemMasterDto findByJanCode(String janCode) {

        ItemMasterEntity e =
            itemMasterRepository.findByJanCode(janCode);

        if (e == null) {
            return null;
        }

        return new ItemMasterDto(
            e.getJanCode(),
            e.getItemMaker(),
            e.getItemName(),
            e.getItemModel()
        );
    }
}