package step;

import model.Point;

public class StepPoint extends Point implements Comparable<StepPoint> {
    public StepPoint(double x, double y) {
        super(x, y);
    }

    @Override
    public int compareTo(StepPoint o) {
        return Double.compare(this.getX(), o.getX());
    }
}