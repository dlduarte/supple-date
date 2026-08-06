package io.github.dlduarte.suppledate;

import io.github.dlduarte.suppledate.aid.elapsed.UnitFormat;
import io.github.dlduarte.suppledate.aid.elapsed.WritingFormat;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Test {

    public static void main(String[] args) {
        LocalDateTime date = LocalDateTime.of(2024, 5, 17, 14, 30, 0);
        Duration duration = Duration.between(LocalDateTime.now(), date);

        System.out.println("DATA GERADA: " + Temporality.timeElapsedInWriting(duration));

        WritingFormat writingFormat = WritingFormat.of("1m",
                UnitFormat.of("y", false),
                UnitFormat.of("d", false),
                UnitFormat.of("h", false),
                UnitFormat.of("m", false)
        );

        String elapsed = Temporality.timeElapsedInWriting(duration, writingFormat);

        System.out.println("DATA GERADA: " + elapsed);
    }
}