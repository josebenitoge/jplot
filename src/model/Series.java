package model;



import java.awt.*;
import java.util.LinkedList;

public class Series {

    private final String name;
    private final Color color;

    private final LinkedList<Point> dataPoints = new LinkedList<>();

    public Series(String name, Color color) {
        this.name = name;
        this.color = color;
    }
    public void add(Point dataPoint){
        dataPoints.add(dataPoint);
    }

    public Color color() { return color; }
    public String name() { return name; }
    // Getters
    public LinkedList<Point> points() { return dataPoints; }
}
