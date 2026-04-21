package polyline;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PolylineDataset extends model.Dataset {

    private final List<String> xCategories = new ArrayList<>();

    @Override
    public void create(Color color, String... config) {
        if (config != null && config.length % 2 != 0) {
            System.err.println("Error: 'create' exige argumentos en pares clave-valor.");
            return;
        }

        String name = "Polyline_" + System.currentTimeMillis();
        String marker = PolylineSeries.MARKER_CIRCLE;

        if (config != null) {
            for (int i = 0; i < config.length; i += 2) {
                String key = config[i].toLowerCase();
                String value = config[i + 1];

                if ("name".equals(key)) name = value;
                if ("marker".equals(key)) marker = value.toUpperCase();
            }
        }

        Color finalColor = (color != null) ? color : randomColor();
        series.put(name, new PolylineSeries(name, finalColor, marker));
    }

    @Override
    public void add(String seriesName, double... coords) {
        if (coords.length < 2) return;
        
        updateLimits(coords[0], coords[1]);

        if (series.containsKey(seriesName)) {
            series.get(seriesName).add(new PolylinePoint(coords[0], coords[1]));
        } else {
            System.err.println("Advertencia: La serie '" + seriesName + "' no existe.");
        }
    }

    // --- IMPLEMENTACIÓN DEL GANCHO PARA TEXTOS ---
    @Override
    public void add(String seriesName, String category, double value) {
        if (!xCategories.contains(category)) {
            xCategories.add(category);
        }
        
        // Mapeamos el texto a su posición en el array (0, 1, 2...)
        double xIndex = xCategories.indexOf(category);
        
        // Llamamos al método numérico
        add(seriesName, xIndex, value);
    }

    public List<String> getXCategories() {
        return xCategories;
    }
}