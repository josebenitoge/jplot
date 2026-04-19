package stackedbar;

import model.Dataset;
import model.Point;
import model.Series;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackedBarDataset extends Dataset {

    private final List<String> categories = new ArrayList<>();
    // Acumulador para saber la altura total de cada columna
    private final Map<String, Double> categorySums = new HashMap<>();

    public List<String> getCategories() {
        return categories;
    }

    @Override
    public void create(Color color, String... config) {
        if (config.length % 2 != 0) return;
        String name = "Stack_" + System.currentTimeMillis();
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

        double xIndex = categories.indexOf(category);

        if (series.containsKey(seriesName)) {
            series.get(seriesName).add(new Point(xIndex, value));

            // Calculamos la suma actual de esta categoría para el límite del Eje Y
            double currentSum = categorySums.getOrDefault(category, 0.0) + value;
            categorySums.put(category, currentSum);
            
            if (currentSum > maxY) maxY = currentSum;
        }
    }

    @Override
    public void add(String seriesName, double... x) {}

    @Override
    public double getMaxY() {
        double actualMax = super.getMaxY();
        if (actualMax <= 0) return 1.0;
        return actualMax * 1.15; // 15% de margen superior
    }
}