package com.kyouseipro.neo.domain.master.item.application;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.domain.master.item.model.ItemMasterDto;
import com.kyouseipro.neo.domain.master.item.model.ItemMasterEntity;
import com.kyouseipro.neo.domain.master.item.repository.ItemMasterRepository;

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