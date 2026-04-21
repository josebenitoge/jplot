package heatmap;

import model.Series;

import java.awt.*;
import java.util.LinkedList;

public class HeatmapSeries extends Series {

    private final String name;
    private final Color color;
    private final LinkedList<HeatmapPoint> dots = new LinkedList<>();

    public HeatmapSeries(String name, Color color) {
        super(name, color);
        this.name = name;
        this.color = color;
    }
}