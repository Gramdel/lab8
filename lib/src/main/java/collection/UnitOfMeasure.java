package collection;

import java.io.Serializable;

public enum UnitOfMeasure implements Serializable {
    KILOGRAMS,
    SQUARE_METERS,
    PCS,
    LITERS,
    MILLILITERS;

    public static UnitOfMeasure fromString(String s) {
        if (s != null) {
            for (UnitOfMeasure unit : UnitOfMeasure.values()) {
                if (s.equals(unit.toString())) {
                    return unit;
                }
            }
        }
        throw new IllegalArgumentException();
    }

    public static String valueList() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < values().length; i++) {
            s.append(values()[i]).append((i != values().length - 1) ? ", " : "");
        }
        return s.toString();
    }
}