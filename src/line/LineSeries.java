package line;

import model.Series;

import java.awt.Color;
import java.util.LinkedList;

public class LineSeries extends Series {


    private final String type;


    private final String style;



    public LineSeries(String name, String type, Color color, String style) {
        super(name, color );
        this.type = type;
        this.style = style;
    }




    public String getType() { return type; }

    public String getStyle() { return style; }
}