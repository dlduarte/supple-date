package io.github.dlduarte.suppledate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZoneId;

@Getter
@RequiredArgsConstructor
public enum Chronos {
	BASIC_D4("ddMMyyyy"),
	BASIC_D4HMS("ddMMyyyyHHmmss"),
	BASIC_D2("ddMMyy"),
	BASIC_D2HMS("ddMMyyHHmmss"),
	BASIC_D4_ISO("yyyyMMdd"),
	BASIC_D4HMS_ISO("yyyyMMddHHmmss"),
	BASIC_D2_ISO("yyMMdd"),
	BASIC_D2HMS_ISO("yyMMddHHmmss"),
	BASIC_HM("HHmm"),

	BAR_D4("dd/MM/yyyy"),
	BAR_D4HM("dd/MM/yyyy HH:mm"),
	BAR_D4HMS("dd/MM/yyyy HH:mm:ss"),
	BAR_D2("dd/MM/yy"),
	BAR_D2HM("dd/MM/yy HH:mm"),
	BAR_D2HMS("dd/MM/yy HH:mm:ss"),
	BAR_D4_ISO("yyyy/MM/dd"),
	BAR_D4HM_ISO("yyyy/MM/dd HH:mm"),
	BAR_D4HMS_ISO("yyyy/MM/dd HH:mm:ss"),
	BAR_D2_ISO("yy/MM/dd"),

	TRACE_D4("dd-MM-yyyy"),
	TRACE_D4HM("dd-MM-yyyy HH:mm"),
	TRACE_D4HMS("dd-MM-yyyy HH:mm:ss"),
	TRACE_D2("dd-MM-yy"),
	TRACE_D2HM("dd-MM-yy HH:mm"),
	TRACE_D2HMS("dd-MM-yy HH:mm:ss"),
	TRACE_D4_ISO("yyyy-MM-dd"),
	TRACE_D4HM_ISO("yyyy-MM-dd HH:mm"),
	TRACE_D4HMS_ISO("yyyy-MM-dd HH:mm:ss"),
	TRACE_D2_ISO("yy-MM-dd"),

	FILE_D2HM("yy-MM-dd HH mm"),
	FILE_D4HM("yyyy-MM-dd HH mm");

	private final String pattern;

	public Temporality of(Object date, ZoneId zoneId) {
		return Temporality.of(date, pattern, zoneId);
	}

	public Temporality of(Object date) {
		return Temporality.of(date, pattern);
	}

	public Temporality now(ZoneId zoneId) {
		return Temporality.now(pattern, zoneId);
	}

	public Temporality now() {
		return Temporality.now(pattern);
	}
}