package io.github.dlduarte.suppledate.aid.elapsed;

import io.github.dlduarte.suppledate.aid.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class WritingFormat {

    private FormattingOption formattingOption;
    private String ifNow;

    private UnitFormat year;
    private UnitFormat day;
    private UnitFormat hour;
    private UnitFormat minute;
    private UnitFormat second;

    public static WritingFormat of(String ifNow) {
        return new WritingFormat(FormattingOption.of(",", " e"), ifNow, null, null, null, null, null);
    }

    public static WritingFormat of(String ifNow, UnitFormat year) {
        return new WritingFormat(FormattingOption.of(",", " e"), ifNow, year, null, null, null, null);
    }

    public static WritingFormat of(String ifNow, UnitFormat year, UnitFormat day) {
        return new WritingFormat(FormattingOption.of(",", " e"), ifNow, year, day, null, null, null);
    }

    public static WritingFormat of(String ifNow, UnitFormat year, UnitFormat day, UnitFormat hour) {
        return new WritingFormat(FormattingOption.of(",", " e"), ifNow, year, day, hour, null, null);
    }

    public static WritingFormat of(String ifNow, UnitFormat year, UnitFormat day, UnitFormat hour, UnitFormat minute) {
        return new WritingFormat(FormattingOption.of(",", " e"), ifNow, year, day, hour, minute, null);
    }

    public static WritingFormat of(String ifNow, UnitFormat year, UnitFormat day, UnitFormat hour, UnitFormat minute, UnitFormat second) {
        return new WritingFormat(FormattingOption.of(",", " e"), ifNow, year, day, hour, minute, second);
    }

    public static WritingFormat of(FormattingOption formattingOption, String ifNow) {
        return new WritingFormat(formattingOption, ifNow, null, null, null, null, null);
    }

    public static WritingFormat of(FormattingOption formattingOption, String ifNow, UnitFormat year) {
        return new WritingFormat(formattingOption, ifNow, year, null, null, null, null);
    }

    public static WritingFormat of(FormattingOption formattingOption, String ifNow, UnitFormat year, UnitFormat day) {
        return new WritingFormat(formattingOption, ifNow, year, day, null, null, null);
    }

    public static WritingFormat of(FormattingOption formattingOption, String ifNow, UnitFormat year, UnitFormat day, UnitFormat hour) {
        return new WritingFormat(formattingOption, ifNow, year, day, hour, null, null);
    }

    public static WritingFormat of(FormattingOption formattingOption, String ifNow, UnitFormat year, UnitFormat day, UnitFormat hour, UnitFormat minute) {
        return new WritingFormat(formattingOption, ifNow, year, day, hour, minute, null);
    }

    public static WritingFormat of(FormattingOption formattingOption, String ifNow, UnitFormat year, UnitFormat day, UnitFormat hour, UnitFormat minute, UnitFormat second) {
        return new WritingFormat(formattingOption, ifNow, year, day, hour, minute, second);
    }

    public String write(Duration duration) {
        long seconds = duration.getSeconds() % 60;
        long minutes = duration.toMinutes() % 60;
        long hours = duration.toHours() % 24;
        long days = duration.toDays() % 365;
        long years = duration.toDays() / 365;

        return write(years, days, hours, minutes, seconds);
    }

    public String write(long years, long days, long hours, long minutes, long seconds) {
        if (years == 0 && days == 0 && hours == 0 && minutes < 1) {
            return ifNow;
        }

        List<Pair<UnitFormat, Long>> pairs = new ArrayList<>();

        setPair(pairs, year, years);
        setPair(pairs, day, days);
        setPair(pairs, hour, hours);
        setPair(pairs, minute, minutes);
        setPair(pairs, second, seconds);

        if (pairs.isEmpty()) {
            return ifNow;
        }

        return formattingOption.format(pairs);
    }

    private void setPair(List<Pair<UnitFormat, Long>> pairs, UnitFormat unit, long value) {
        if (unit != null && value > 0) {
            pairs.add(Pair.of(unit, value));
        }
    }
}