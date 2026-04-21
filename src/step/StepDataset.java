package step;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class StepDataset extends model.Dataset {

    private final List<String> xCategories = new ArrayList<>();

    @Override
    public void create(Color color, String... config) {
        String name = "StepSeries_" + System.currentTimeMillis();
        
        if (config != null) {
            for (int i = 0; i < config.length - 1; i += 2) {
                if ("name".equalsIgnoreCase(config[i])) {
                    name = config[i + 1];
                }
            }
        }

        Color finalColor = (color != null) ? color : new Color(0, 85, 140); // Azul clásico por defecto
        series.put(name, new StepSeries(name, finalColor));
    }

    @Override
    public void add(String seriesName, double... coords) {
        if (coords.length < 2) return;
        updateLimits(coords[0], coords[1]);

        if (series.containsKey(seriesName)) {
            series.get(seriesName).add(new StepPoint(coords[0], coords[1]));
        }
    }

    @Override
    public void add(String seriesName, String category, double value) {
        if (!xCategories.contains(category)) {
            xCategories.add(category);
        }
        double xIndex = xCategories.indexOf(category);
        add(seriesName, xIndex, value);
    }

    public List<String> getXCategories() {
        return xCategories;
    }
}