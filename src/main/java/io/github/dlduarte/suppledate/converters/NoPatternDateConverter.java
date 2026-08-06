package io.github.dlduarte.suppledate.converters;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

public interface NoPatternDateConverter<T> extends PatternDateConverter<T> {
	LocalDateTime toLocalDateTime(@NonNull final T date, final ZoneId zoneId);

	T fromLocalDateTime(@NonNull final LocalDateTime date, final ZoneId zoneId);

	default LocalDateTime toLocalDateTime(@NonNull final T date) {
		return toLocalDateTime(date, ZoneId.systemDefault());
	}

	default T fromLocalDateTime(@NonNull final LocalDateTime date) {
		return fromLocalDateTime(date, ZoneId.systemDefault());
	}

	@Override
	default LocalDateTime toLocalDateTime(@NonNull final T date, final String pattern, final ZoneId zoneId) {
		return toLocalDateTime(date, zoneId);
	}

	@Override
	default T fromLocalDateTime(@NonNull final LocalDateTime date, final String pattern, final ZoneId zoneId) {
		return fromLocalDateTime(date, zoneId);
	}
}
