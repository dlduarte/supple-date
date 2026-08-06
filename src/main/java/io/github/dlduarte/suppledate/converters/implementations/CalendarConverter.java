package io.github.dlduarte.suppledate.converters.implementations;

import io.github.dlduarte.suppledate.converters.NoPatternDateConverter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

public class CalendarConverter implements NoPatternDateConverter<Calendar> {
	@Override
	public LocalDateTime toLocalDateTime(@NonNull Calendar date, ZoneId zoneId) {
		return LocalDateTime.ofInstant(date.toInstant(), zoneId);
	}

	@Override
	public Calendar fromLocalDateTime(@NonNull LocalDateTime date, ZoneId zoneId) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new DateConverter().fromLocalDateTime(date, zoneId));
		return calendar;
	}
}
