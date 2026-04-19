package pie;

import model.Dataset;
import model.Point;
import model.Series;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PieDataset extends Dataset {

    private final List<String> categories = new ArrayList<>();

    @Override
    public void create(Color color, String... config) {
        String name = (config.length > 1 && config[0].equalsIgnoreCase("name")) ? config[1] : "Pie_" + System.currentTimeMillis();
        series.put(name, new Series(name, color != null ? color : randomColor()));
    }

    @Override
    public void add(String seriesName, String category, double value) {
        if (!categories.contains(category)) categories.add(category);
        if (series.containsKey(seriesName)) {
            // Guardamos la categoría en X (índice) y el valor en Y
            series.get(seriesName).add(new Point(categories.indexOf(category), value));
        }
    }

    @Override
    public void add(String series, double... x) {}

    public List<String> getCategories() { return categories; }
}