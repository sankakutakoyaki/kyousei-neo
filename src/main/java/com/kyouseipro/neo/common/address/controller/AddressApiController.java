package com.kyouseipro.neo.common.address.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kyouseipro.neo.common.address.entity.AddressEntity;
import com.kyouseipro.neo.common.address.repository.AddressRepository;
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.dto.StringRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class AddressApiController {
    private final AddressRepository addressRepository;

    @PostMapping("/get/postalcode")
    public ResponseEntity<SimpleResponse<AddressEntity>> getEntityByPostalCode(@RequestBody StringRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(addressRepository.findByPostalCode(req.getValue())));
    }
}
