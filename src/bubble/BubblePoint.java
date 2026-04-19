package bubble;

import model.Point;

public class BubblePoint extends Point {


    private final double x;
    private final double y;
    private final double z;

    public BubblePoint(double x, double y, double z) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
