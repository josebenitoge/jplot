package histogram;

import model.Point;
import model.Series;
import java.awt.Color;

public class HistogramDataset extends model.Dataset {

    @Override
    public void create(Color color, String... config) {
        String name = "Histogram_" + System.currentTimeMillis();
        int bins = 40;

        if (config != null) {
            for (int i = 0; i < config.length - 1; i += 2) {
                String key = config[i].toLowerCase();
                String value = config[i + 1];
                if ("name".equals(key)) name = value;
                if ("bins".equals(key)) bins = Integer.parseInt(value);
            }
        }

        Color finalColor = (color != null) ? color : new Color(0, 85, 140);
        series.put(name, new HistogramSeries(name, finalColor, bins));
    }

    @Override
    public void add(String seriesName, double... coords) {
        if (coords.length < 1) return;
        HistogramSeries s = (HistogramSeries) series.get(seriesName);
        if (s != null) {
            if (coords.length == 1) {
                // Modo Auto-Binning (Un solo valor)
                s.addRawValue(coords[0]);
            } else {
                // Modo Manual (Centro de Bin, Frecuencia)
                s.add(new HistogramPoint(coords[0], coords[1]));
            }
        }
    }

    /**
     * Compila los datos en bruto y recalcula los límites globales del gráfico.
     * Debe llamarse antes de renderizar.
     */
    public void compileData() {
        minX = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        minY = 0.0; // El histograma siempre descansa sobre Y=0
        maxY = -Double.MAX_VALUE;

        for (Series baseSet : series.values()) {
            HistogramSeries set = (HistogramSeries) baseSet;
            set.computeBins();
            
            for (Point p : set.points()) {
                if (p.getX() < minX) minX = p.getX();
                if (p.getX() > maxX) maxX = p.getX();
                if (p.getY() > maxY) maxY = p.getY();
            }
        }
        
        // Protecciones matemáticas
        if (minX == Double.MAX_VALUE) minX = -1;
        if (maxX == -Double.MAX_VALUE) maxX = 1;
        if (maxY <= 0) maxY = 1;
    }
}