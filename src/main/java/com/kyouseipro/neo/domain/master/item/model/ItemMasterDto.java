package com.kyouseipro.neo.domain.master.item.model;

public record ItemMasterDto(
    String janCode,
    String itemMaker,
    String itemName,
    String itemModel
) {}