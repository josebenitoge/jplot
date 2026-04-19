package bubble;

import model.Series;

import java.awt.Color;
import java.util.LinkedList;

public class BubbleSeries extends Series {

    private final String name;
    private final Color color;
    private final LinkedList<BubblePoint> dots = new LinkedList<>();

    public BubbleSeries(String name, Color color) {
        super(name, color);
        this.name = name;
        this.color = color;
    }
}