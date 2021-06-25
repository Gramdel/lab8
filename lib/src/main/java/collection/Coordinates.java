package collection;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private final Double x; //Поле не может быть null
    private final Long y; //Поле не может быть null

    public Coordinates(Double x, Long y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + "}";
    }

    public Double getX() {
        return x;
    }

    public Long getY() {
        return y;
    }
}