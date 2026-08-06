package io.github.dlduarte.suppledate.converters.implementations;

import io.github.dlduarte.suppledate.converters.NoPatternDateConverter;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateConverter implements NoPatternDateConverter<LocalDate> {
	@Override
	public LocalDateTime toLocalDateTime(@NonNull LocalDate date, ZoneId zoneId) {
		return date.atStartOfDay().atZone(zoneId).toLocalDateTime();
	}

	@Override
	public LocalDate fromLocalDateTime(@NonNull LocalDateTime date, ZoneId zoneId) {
		return date.atZone(zoneId).toLocalDate();
	}
}
