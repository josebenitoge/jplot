package model;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public abstract class Dataset {

    protected final LinkedHashMap<String, Series> series = new LinkedHashMap<>();

    protected double minX = Double.MAX_VALUE;
    protected double maxX = -Double.MAX_VALUE;
    protected double minY = Double.MAX_VALUE;
    protected double maxY = -Double.MAX_VALUE;

    protected static final Random rand = new Random();

    public abstract void create(Color color, String... config);
    public abstract void add(String series, double... x);

    // ¡NUEVO! Método para gráficos categóricos (Barras)
    // Lo dejamos vacío para que los hijos que no lo usen (Líneas/Burbujas) no tengan que implementarlo por obligación.
    public void add(String series, String category, double value) {}

    protected void updateLimits(double x, double y) {
        if (x > maxX) maxX = x;
        if (x < minX) minX = x;
        if (y > maxY) maxY = y;
        if (y < minY) minY = y;
    }

    public HashMap<String, Series> series() { return series; }
    public double getMaxX() { return maxX; }
    public double getMaxY() { return maxY; }
    public double getMinX() { return minX; }
    public double getMinY() { return minY; }

    protected Color randomColor() {
        float hue = rand.nextFloat();
        float saturation = 0.3f + (rand.nextFloat() * 0.3f);
        float brightness = 0.6f + (rand.nextFloat() * 0.3f);
        return Color.getHSBColor(hue, saturation, brightness);
    }
}