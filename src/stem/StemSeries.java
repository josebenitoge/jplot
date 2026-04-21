package stem;

import model.Series;
import java.awt.Color;

public class StemSeries extends Series {

    private final String style;

    public StemSeries(String name, Color color, String style) {
        super(name, color);
        this.style = style;
    }

    public String getStyle() { 
        return style; 
    }
}