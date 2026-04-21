package histogram;

import model.Series;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistogramSeries extends Series {

    private final List<Double> rawData = new ArrayList<>();
    private int binCount = 40; // Número de barras por defecto (similar a la imagen)

    public HistogramSeries(String name, Color color, int binCount) {
        super(name, color);
        if (binCount > 0) this.binCount = binCount;
    }

    public void addRawValue(double value) {
        rawData.add(value);
    }

    /**
     * Calcula la distribución de frecuencias y genera los puntos (X=Centro, Y=Frecuencia).
     */
    public void computeBins() {
        if (rawData.isEmpty()) return;
        points().clear(); // Limpiamos cálculos anteriores

        double min = Collections.min(rawData);
        double max = Collections.max(rawData);

        if (min == max) {
            add(new HistogramPoint(min, rawData.size()));
            return;
        }

        double binWidth = (max - min) / binCount;
        int[] counts = new int[binCount];

        for (double v : rawData) {
            int idx = (int) ((v - min) / binWidth);
            if (idx >= binCount) idx = binCount - 1; // Proteger límite superior
            if (idx < 0) idx = 0;
            counts[idx]++;
        }

        for (int i = 0; i < binCount; i++) {
            double binCenter = min + (i + 0.5) * binWidth;
            add(new HistogramPoint(binCenter, counts[i]));
        }
    }
}