package com.kyouseipro.neo.sql.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.service.SelectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/select")
@RequiredArgsConstructor
public class SelectController {
    private final SelectService selectService;

    @PostMapping
    public List<Map<String, Object>> select(@RequestBody SelectRequest req) {
        return selectService.select(req);
    }

}
