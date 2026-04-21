package polyline;

import model.Series;
import java.awt.Color;

public class PolylineSeries extends Series {

    public static final String MARKER_CIRCLE = "CIRCLE";
    public static final String MARKER_NONE = "NONE";

    private final String markerStyle;

    public PolylineSeries(String name, Color color, String markerStyle) {
        super(name, color);
        this.markerStyle = markerStyle != null ? markerStyle : MARKER_CIRCLE;
    }

    public String getMarkerStyle() {
        return markerStyle;
    }
}