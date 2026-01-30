package com.kyouseipro.neo.common.address.entity;

import lombok.Data;

@Data
public class AddressEntity {

    private int addressId;
    private int addressCode;
    private int prefectureCode;
    private int cityCode;
    private int townCode;
    private String postalCode;
    private String prefecture;
    private String prefectureKana;
    private String city;
    private String cityKana;
    private String town;
    private String townKana;
}