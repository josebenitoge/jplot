package polyline;

import model.Point;

public class PolylinePoint extends Point implements Comparable<PolylinePoint> {

    public PolylinePoint(double x, double y) {
        super(x, y);
    }

    @Override
    public int compareTo(PolylinePoint o) {
        return Double.compare(this.getX(), o.getX());
    }
}