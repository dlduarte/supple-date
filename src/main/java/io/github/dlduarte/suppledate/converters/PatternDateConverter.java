package io.github.dlduarte.suppledate.converters;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

public interface PatternDateConverter<T> {
	LocalDateTime toLocalDateTime(@NonNull final T date, final String pattern, @NonNull final ZoneId zoneId);

	T fromLocalDateTime(@NonNull final LocalDateTime date, final String pattern, @NonNull final ZoneId zoneId);

	default LocalDateTime toLocalDateTime(@NonNull final T date, final String pattern) {
		return toLocalDateTime(date, pattern, ZoneId.systemDefault());
	}

	default T fromLocalDateTime(@NonNull final LocalDateTime date, final String pattern) {
		return fromLocalDateTime(date, pattern, ZoneId.systemDefault());
	}
}
