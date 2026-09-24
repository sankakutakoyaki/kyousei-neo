package com.kyouseipro.neo.domain.management.timeworks.model;

import java.time.LocalDate;

public record TimeworkPeriod(LocalDate from, LocalDate to, String label) {
}
