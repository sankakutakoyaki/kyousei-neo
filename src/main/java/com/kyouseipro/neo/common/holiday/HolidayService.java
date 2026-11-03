package com.kyouseipro.neo.common.holiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class HolidayService {

    private static final String CSV_PATH = "data/holidays/syukujitsu.csv";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/M/d");

    private volatile Map<LocalDate, String> holidays;

    private final String csvPath;

    public HolidayService(@Value("${holiday.csv.path:}") String csvPath) {
        this.csvPath = csvPath;
        reload();
    }

    public synchronized void reload() {
        Map<LocalDate, String> updated = loadHolidays();
        if(updated.isEmpty()){
            throw new IllegalStateException("祝日データが空です");
        }
        holidays = Collections.unmodifiableMap(updated);
    }

    public String getHolidayName(LocalDate date) {
        return holidays.get(date);
    }

    public boolean isHoliday(LocalDate date) {
        return holidays.containsKey(date);
    }

    public Map<String, String> getHolidays(int year) {
        return holidays.entrySet().stream()
            .filter(entry -> entry.getKey().getYear() == year)
            .collect(Collectors.toMap(
                entry -> entry.getKey().toString(),
                Map.Entry::getValue
            ));
    }

    private Map<LocalDate, String> loadHolidays() {
        Map<LocalDate, String> result = new HashMap<>();

        try (BufferedReader reader = createReader()) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] columns = line.split(",", 2);
                if (columns.length != 2) {
                    throw new IllegalStateException("祝日CSVの形式が不正です: " + line);
                }
                LocalDate date = LocalDate.parse(columns[0].trim(), DATE_FORMAT);
                result.put(date, columns[1].trim());
            }
        } catch (Exception e) {
            throw new IllegalStateException("祝日CSVの読み込みに失敗しました", e);
        }
        return result;
    }

    private BufferedReader createReader() throws IOException {
        if (csvPath != null && !csvPath.isBlank()) {
            Path path = Path.of(csvPath);
            if (Files.isRegularFile(path)) {
                return Files.newBufferedReader(path, StandardCharsets.UTF_8);
            }
        }
        Resource resource = new ClassPathResource(CSV_PATH);
        return new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );
    }
}