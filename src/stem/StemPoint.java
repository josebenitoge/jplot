package stem;

import model.Point;

public class StemPoint extends Point {

    private final double x;
    private final double y;

    public StemPoint(double x, double y) {
        super(x, y);
        this.x = x;
        this.y = y;
    }
}