package bar;

import model.Dataset;
import model.Point;
import model.Series;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class BarDataset extends Dataset {

    // Guardamos las categorías en orden para el eje X
    private final List<String> categories = new ArrayList<>();

    public List<String> getCategories() {
        return categories;
    }

    @Override
    public void create(Color color, String... config) {
        if (config.length % 2 != 0) return;
        String name = "Bar_" + System.currentTimeMillis();
        for (int i = 0; i < config.length; i += 2) {
            if (config[i].equalsIgnoreCase("name")) name = config[i + 1];
        }
        Color finalColor = (color != null) ? color : randomColor();
        series.put(name, new Series(name, finalColor));
    }

    @Override
    public void add(String seriesName, double... x) {
        // No usamos este en barras, pero estamos obligados a implementarlo
    }

    // ¡USAMOS EL NUEVO MÉTODO!
    @Override
    public void add(String seriesName, String category, double value) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
        
        // La X será el índice de la categoría (0, 1, 2...)
        double xIndex = categories.indexOf(category);

        // Actualizamos límites (minY usualmente lo forzamos a 0 en barras más adelante)
        updateLimits(xIndex, value);

        if (series.containsKey(seriesName)) {
            series.get(seriesName).add(new Point(xIndex, value));
        }
    }
    @Override
    public double getMaxY() {
        double actualMax = super.getMaxY();

        // Evitamos problemas si la gráfica está vacía o todo es cero
        if (actualMax <= 0) return 1.0;

        // Le añadimos un 15% de margen "de aire" por encima a la barra más alta
        return actualMax * 1.15;
    }
}