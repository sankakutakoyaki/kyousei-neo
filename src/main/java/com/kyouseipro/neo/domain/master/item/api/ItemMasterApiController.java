package com.kyouseipro.neo.domain.master.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.kyouseipro.neo.common.request.StringRequest;
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.domain.master.item.model.ItemMasterDto;
import com.kyouseipro.neo.domain.master.item.application.ItemMasterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemMasterApiController {

    private final ItemMasterService itemMasterService;

    @PostMapping("/get/jancode")
    public ResponseEntity<SimpleResponse<ItemMasterDto>> getByJanCode(
        @RequestBody StringRequest req
    ) {

        ItemMasterDto dto =
            itemMasterService.findByJanCode(req.getValue());

        return ResponseEntity.ok(SimpleResponse.ok(dto));
    }
}