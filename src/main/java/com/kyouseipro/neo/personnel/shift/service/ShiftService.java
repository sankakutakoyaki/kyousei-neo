package com.kyouseipro.neo.personnel.shift.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.repository.EmployeeRepository;
import com.kyouseipro.neo.personnel.shift.entity.PlannedShift;
import com.kyouseipro.neo.personnel.shift.repository.PlannedShiftRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final PlannedShiftRepository repository;
    private final EmployeeRepository employeeRepository;

    public Map<String, Object> getMonth(int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<EmployeeEntity> employees = employeeRepository.findAll();
        List<PlannedShift> shifts = repository.findByMonth(start, end);

        Map<Long, Map<LocalDate, String>> shiftMap = new HashMap<>();

        for (PlannedShift s : shifts) {
            shiftMap
                .computeIfAbsent(s.getEmployeeId(), k -> new HashMap<>())
                .put(s.getShiftDate(), s.getShiftType());
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (EmployeeEntity emp : employees) {
            Map<String, Object> row = new HashMap<>();
            row.put("employeeId", emp.getEmployeeId());
            row.put("fullName", emp.getFullName());
            row.put("shifts", shiftMap.getOrDefault(emp.getEmployeeId(), Map.of()));
            result.add(row);
        }

        return Map.of("employees", result);
    }

    public void change(Long employeeId, LocalDate date, String type) {

        if (type == null || type.isBlank()) {
            repository.delete(employeeId, date);
            return;
        }

        if (repository.exists(employeeId, date)) {
            repository.update(employeeId, date, type);
        } else {
            repository.insert(employeeId, date, type);
        }
    }
}