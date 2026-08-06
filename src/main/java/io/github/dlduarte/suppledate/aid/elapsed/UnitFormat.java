package io.github.dlduarte.suppledate.aid.elapsed;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnitFormat {

    private String single;
    private String plural;
    private boolean space;

    public static UnitFormat of(String single) {
        return new UnitFormat(single, single, true);
    }

    public static UnitFormat of(String single, String plural) {
        return new UnitFormat(single, plural, true);
    }

    public static UnitFormat of(String single, boolean space) {
        return new UnitFormat(single, single, space);
    }

    public static UnitFormat of(String single, String plural, boolean space) {
        return new UnitFormat(single, plural, space);
    }

    public String getSingle() {
        return space ? " " + single : single;
    }

    public String getPlural() {
        return space ? " " + plural : plural;
    }
}
