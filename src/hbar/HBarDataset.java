package hbar;

import model.Dataset;
import model.Point;
import model.Series;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class HBarDataset extends Dataset {

    private final List<String> categories = new ArrayList<>();

    public List<String> getCategories() {
        return categories;
    }

    @Override
    public void create(Color color, String... config) {
        if (config.length % 2 != 0) return;
        String name = "HBar_" + System.currentTimeMillis();
        for (int i = 0; i < config.length; i += 2) {
            if (config[i].equalsIgnoreCase("name")) name = config[i + 1];
        }
        Color finalColor = (color != null) ? color : randomColor();
        series.put(name, new Series(name, finalColor));
    }

    @Override
    public void add(String seriesName, String category, double value) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
        
        // ¡OJO AL CAMBIO! En horizontales, el Valor es la X, y la Categoría es la Y.
        double yIndex = categories.indexOf(category);

        updateLimits(value, yIndex);

        if (series.containsKey(seriesName)) {
            series.get(seriesName).add(new Point(value, yIndex));
        }
    }

    @Override
    public void add(String seriesName, double... x) {
        // No usado en categóricos
    }

    // ========================================================
    // Damos un 15% de "aire" por la derecha a la barra más larga
    // ========================================================
    @Override
    public double getMaxX() {
        double actualMax = super.getMaxX();
        if (actualMax <= 0) return 1.0; 
        return actualMax * 1.15; 
    }
}