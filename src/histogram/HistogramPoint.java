package histogram;

import model.Point;

public class HistogramPoint extends Point implements Comparable<HistogramPoint> {
    public HistogramPoint(double x, double y) {
        super(x, y);
    }

    @Override
    public int compareTo(HistogramPoint o) {
        return Double.compare(this.getX(), o.getX());
    }
}