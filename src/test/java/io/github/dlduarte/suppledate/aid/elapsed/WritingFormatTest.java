package io.github.dlduarte.suppledate.aid.elapsed;

import io.github.dlduarte.suppledate.Temporality;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WritingFormatTest {

    @Nested
    @DisplayName("Durações que são múltiplos exatos de 365 dias")
    class ExactYears {

        @Test
        @DisplayName("365 dias exatos são escritos como 1 ano, não como 'Agora'")
        void oneExactYear() {
            assertEquals("1 ano", Temporality.timeElapsedInWriting(Duration.ofDays(365)));
        }

        @Test
        @DisplayName("730 dias exatos são escritos como 2 anos")
        void twoExactYears() {
            assertEquals("2 anos", Temporality.timeElapsedInWriting(Duration.ofDays(730)));
        }

        @Test
        @DisplayName("um ano exato com segundos ainda mostra o ano")
        void exactYearWithSeconds() {
            assertEquals("1 ano e 30 segundos",
                    Temporality.timeElapsedInWriting(Duration.ofDays(365).plusSeconds(30)));
        }
    }

    @Nested
    @DisplayName("Durações insignificantes")
    class BelowThreshold {

        @Test
        @DisplayName("abaixo de um minuto usa o texto de 'agora'")
        void underOneMinute() {
            assertEquals("Agora", Temporality.timeElapsedInWriting(Duration.ofSeconds(30)));
            assertEquals("Agora", Temporality.timeElapsedInWriting(Duration.ofSeconds(59)));
            assertEquals("Agora", Temporality.timeElapsedInWriting(Duration.ZERO));
        }

        @Test
        @DisplayName("um minuto cheio já é escrito")
        void exactlyOneMinute() {
            assertEquals("1 minuto", Temporality.timeElapsedInWriting(Duration.ofSeconds(60)));
        }

        @Test
        @DisplayName("quando nenhuma unidade configurada tem valor, cai no texto de 'agora'")
        void noRepresentableUnit() {
            WritingFormat onlyDays = WritingFormat.of("hoje",
                    UnitFormat.of("ano", "anos"),
                    UnitFormat.of("dia", "dias"));

            assertEquals("hoje", Temporality.timeElapsedInWriting(Duration.ofHours(3), onlyDays));
        }
    }

    @Nested
    @DisplayName("Formatação")
    class Formatting {

        private final Duration mixed = Duration.ofDays(400).plusHours(3).plusMinutes(5).plusSeconds(30);

        @Test
        @DisplayName("formato padrão em português")
        void defaultFormat() {
            assertEquals("1 ano, 35 dias, 3 horas, 5 minutos e 30 segundos",
                    Temporality.timeElapsedInWriting(mixed));
        }

        @Test
        @DisplayName("singular e plural são escolhidos pelo valor")
        void singularAndPlural() {
            assertEquals("1 hora", Temporality.timeElapsedInWriting(Duration.ofHours(1)));
            assertEquals("5 horas e 2 minutos",
                    Temporality.timeElapsedInWriting(Duration.ofHours(5).plusMinutes(2)));
        }

        @Test
        @DisplayName("unidades sem espaço geram texto compacto")
        void compactUnits() {
            WritingFormat compact = WritingFormat.of("1m",
                    UnitFormat.of("y", false),
                    UnitFormat.of("d", false),
                    UnitFormat.of("h", false),
                    UnitFormat.of("m", false));

            assertEquals("1y, 35d, 3h e 5m", Temporality.timeElapsedInWriting(mixed, compact));
        }

        @Test
        @DisplayName("separadores customizados são respeitados")
        void customSeparators() {
            WritingFormat slashes = WritingFormat.of(
                    FormattingOption.of(" /"),
                    "now",
                    UnitFormat.of("y"), UnitFormat.of("d"), UnitFormat.of("h"), UnitFormat.of("m"));

            assertEquals("1 y / 2 d / 3 h / 4 m", slashes.write(1, 2, 3, 4, 0));
        }

        @Test
        @DisplayName("unidades não configuradas somem do texto")
        void reducedGranularity() {
            WritingFormat onlyDays = WritingFormat.of("hoje",
                    UnitFormat.of("ano", "anos"),
                    UnitFormat.of("dia", "dias"));

            assertEquals("1 ano e 35 dias", Temporality.timeElapsedInWriting(mixed, onlyDays));
        }
    }
}
