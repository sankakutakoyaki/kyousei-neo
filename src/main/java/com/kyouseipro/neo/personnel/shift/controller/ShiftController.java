package com.kyouseipro.neo.personnel.shift.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.personnel.shift.service.ShiftService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shift")
public class ShiftController {

    private final ShiftService service;

    @GetMapping("")
    public String view() {
        return "shift";
    }

    @GetMapping("/api/month")
    @ResponseBody
    public Map<String, Object> month(
            @RequestParam int year,
            @RequestParam int month) {

        return service.getMonth(year, month);
    }

    @PostMapping("/api/change")
    @ResponseBody
    public void change(@RequestBody Map<String, String> req) {

        Long empId = Long.valueOf(req.get("employeeId"));
        LocalDate date = LocalDate.parse(req.get("date"));
        String type = req.get("shiftType");

        service.change(empId, date, type);
    }
}