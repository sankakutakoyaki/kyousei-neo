package com.kyouseipro.neo.domain.management.model;

import java.time.LocalDate;

public record TimeworkPeriod(LocalDate from, LocalDate to, String label) {
}
