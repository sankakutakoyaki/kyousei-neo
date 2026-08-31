package com.kyouseipro.neo.domain.business.api.order;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.common.response.SimpleResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order/ocr-layout")
public class OrderOcrLayoutController {
    private final OrderOcrLayoutRepository repository;

    @GetMapping
    public List<OrderOcrLayout> find(@RequestParam long primeConstractorId) { return repository.find(primeConstractorId); }

    @PostMapping
    public SimpleResponse<Void> save(@RequestParam long primeConstractorId, @RequestBody List<OrderOcrLayout> layouts) {
        repository.save(primeConstractorId, layouts);
        return SimpleResponse.ok("帳票設定を保存しました。", null);
    }
}
