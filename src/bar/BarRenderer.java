package bar;

import model.Dataset;
import model.Pixel;
import model.Point;
import model.Series;
import java.awt.Color;
import java.util.List;

public class BarRenderer {

    private final BarDataset dataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public BarRenderer(Dataset dataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
        this.dataset = (BarDataset) dataset;
        this.pixels = pixels;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public void bind() {
        int targetWidth = right - left;
        int graphHeight = bottom - top;
        if (targetWidth <= 0 || graphHeight <= 0) return;

        List<String> categories = dataset.getCategories();
        int numCategories = categories.size();
        if (numCategories == 0) return;

        // ¡NUEVO! Contamos cuántas series (comparativas) hay en total
        int numSeries = dataset.series().size();
        if (numSeries == 0) return;

        double minY = 0.0;
        double maxY = dataset.getMaxY();
        if (maxY == 0) maxY = 1.0;

        // 1. Calculamos el espacio de cada categoría entera
        double stepX = (double) targetWidth / numCategories;

        // 2. Calculamos el "Grupo" de barras (80% del espacio, 20% de aire)
        double groupWidth = stepX * 0.8;

        // 3. Calculamos cuánto mide UNA sola barra dentro de ese grupo
        double barWidth = groupWidth / numSeries;

        int seriesIndex = 0; // Índice para saber si es la serie 1, 2 o 3...

        for (Series set : dataset.series().values()) {
            Color color = set.color();

            for (Point p : set.points()) {
                int xIndex = (int) p.getX();
                double val = p.getY();

                // Centro absoluto de la categoría
                int categoryCenterX = left + (int) ((xIndex * stepX) + (stepX / 2));

                // ¿Dónde empieza el grupo de barras de esta categoría? (A la izquierda del centro)
                double groupStartX = categoryCenterX - (groupWidth / 2.0);

                // ¿Dónde está el centro de ESTA barra en específico?
                double barCenterX = groupStartX + (seriesIndex * barWidth) + (barWidth / 2.0);

                // Cálculo de altura
                double ratioY = (val - minY) / (maxY - minY);
                int pixelY = bottom - (int) (ratioY * graphHeight);

                // Pintamos el rectángulo
                int startX = (int) (barCenterX - (barWidth / 2.0));
                int endX = (int) (barCenterX + (barWidth / 2.0));

                // Pequeño detalle visual: Si hay más de 1 serie, le quitamos 1px a la derecha para dejar una línea separadora
                if (numSeries > 1) {
                    endX -= 1;
                }

                drawSolidRect(startX, pixelY, endX, bottom, color);
            }
            seriesIndex++; // Pasamos a la siguiente serie para que se dibuje más a la derecha
        }
    }

    private void drawSolidRect(int x0, int y0, int x1, int y1, Color c) {
        int minX = Math.max(left + 1, x0);
        int maxX = Math.min(right - 1, x1);
        int minY = Math.max(top + 1, y0);
        int maxY = Math.min(bottom - 1, y1);

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                pixels[px][py].color = c;
            }
        }
    }
}