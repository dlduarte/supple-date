package io.github.dlduarte.suppledate.converters.implementations;

import io.github.dlduarte.suppledate.converters.PatternDateConverter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class LongConverter implements PatternDateConverter<Long> {
	@Override
	public LocalDateTime toLocalDateTime(@NonNull Long date, String pattern, @NonNull ZoneId zoneId) {
		String stringDate = date.toString();
		return new StringConverter().toLocalDateTime(stringDate, pattern, zoneId);
	}

	@Override
	public Long fromLocalDateTime(@NonNull LocalDateTime date, String pattern, @NonNull ZoneId zoneId) {
		return Long.parseLong(new StringConverter().fromLocalDateTime(date, pattern, zoneId).replaceAll("\\D", ""));
	}
}
