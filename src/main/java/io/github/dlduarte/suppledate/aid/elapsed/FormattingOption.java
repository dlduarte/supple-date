package io.github.dlduarte.suppledate.aid.elapsed;

import io.github.dlduarte.suppledate.aid.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FormattingOption {

    private String separator;
    private String finalSeparator;

    public static FormattingOption of(String separator, String finalSeparator) {
        return new FormattingOption(separator, finalSeparator);
    }

    public static FormattingOption of(String separator) {
        return new FormattingOption(separator, separator);
    }

    public String format(List<Pair<UnitFormat, Long>> list) {
        StringBuilder sb = new StringBuilder();

        int pointer = 1;
        for (Pair<UnitFormat, Long> item : list) {
            String model = "%d%s";

            long value = item.getSecond();
            UnitFormat unit = item.getFirst();

            String sprtr;
            if (pointer == list.size() - 1) {
                sprtr = finalSeparator + " ";
            } else if (pointer == list.size()) {
                sprtr = "";
            } else {
                sprtr = separator + " ";
            }

            sb.append(String.format(model, value, value == 1 ? unit.getSingle() : unit.getPlural())).append(sprtr);

            pointer++;
        }

        return sb.toString();
    }
}
