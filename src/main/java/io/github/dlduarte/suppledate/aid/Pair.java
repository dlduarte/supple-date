package io.github.dlduarte.suppledate.aid;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Pair<L, R> {

    private L first;
    private R second;

    public static <L, R> Pair<L, R> of(L first, R second) {
        return new Pair<>(first, second);
    }
}
