package io.github.dlduarte.suppledate.converters.implementations;

import io.github.dlduarte.suppledate.converters.PatternDateConverter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class StringConverter implements PatternDateConverter<String> {
	@Override
	public LocalDateTime toLocalDateTime(@NonNull String date, String pattern, @NonNull ZoneId zoneId) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.appendPattern(pattern)
				.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
				.toFormatter();

		return LocalDateTime.parse(date, formatter);
	}

	@Override
	public String fromLocalDateTime(@NonNull LocalDateTime date, String pattern, @NonNull ZoneId zoneId) {
		return ZonedDateTime.of(date, zoneId).format(DateTimeFormatter.ofPattern(pattern));
	}
}
