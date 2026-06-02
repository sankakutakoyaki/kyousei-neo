package com.kyouseipro.neo.domain.address.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kyouseipro.neo.common.request.StringRequest;
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.domain.address.entity.AddressDto;
import com.kyouseipro.neo.domain.address.service.AddressService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class AddressApiController {
    private final AddressService addressService;

    @PostMapping("/get/postalcode")
    public ResponseEntity<SimpleResponse<AddressDto>> getEntityByPostalCode(
        @RequestBody StringRequest req
    ) {
        AddressDto dto = addressService.findByPostalCode(req.getValue());
        return ResponseEntity.ok(SimpleResponse.ok(dto));
    }
}