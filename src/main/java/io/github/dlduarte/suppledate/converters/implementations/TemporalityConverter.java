package io.github.dlduarte.suppledate.converters.implementations;

import io.github.dlduarte.suppledate.Temporality;
import io.github.dlduarte.suppledate.converters.PatternDateConverter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TemporalityConverter implements PatternDateConverter<Temporality> {
	@Override
	public LocalDateTime toLocalDateTime(@NonNull Temporality date, String pattern, @NonNull ZoneId zoneId) {
		return date.parse(LocalDateTime.class, zoneId);
	}

	@Override
	public Temporality fromLocalDateTime(@NonNull LocalDateTime date, String pattern, @NonNull ZoneId zoneId) {
		return Temporality.of(date, pattern, zoneId);
	}
}
