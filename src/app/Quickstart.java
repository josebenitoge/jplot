package app;

import ui.Plotter;

import java.awt.*;

public class Quickstart {
    public static void main(String[] args) {
        Plotter plotter = new Plotter("Simple Chart", Plotter.POLYLINE_PLOT, "X Axis", "Y Axis");
        plotter.create(Color.BLUE, "name", "Dataset 1");
        plotter.add("Dataset 1", 1.0, 2.0);
        plotter.add("Dataset 1", 2.0, 4.0);
        plotter.add("Dataset 1", 3.0, 6.0);
        plotter.plot();
    }
}
