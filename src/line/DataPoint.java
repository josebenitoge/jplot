package line;

import model.Point;

public class DataPoint extends Point {

    private final double x;
    private final double y;

    public DataPoint(double x, double y) {
        super(x, y);
        this.x = x;
        this.y = y;
    }
}