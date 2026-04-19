package stackedbar;

import model.Dataset;
import model.Pixel;
import model.Point;
import model.Series;
import java.awt.Color;
import java.util.List;

public class StackedBarRenderer {

    private final StackedBarDataset dataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public StackedBarRenderer(Dataset dataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
        this.dataset = (StackedBarDataset) dataset;
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
        int numBars = categories.size();
        if (numBars == 0) return;

        double maxY = dataset.getMaxY();
        if (maxY == 0) maxY = 1.0;

        double stepX = (double) targetWidth / numBars;
        double barWidth = stepX * 0.55; // 55% de ancho para parecerse a la imagen de los pingüinos

        // Acumulador de Y en la pantalla
        double[] categoryAccumulators = new double[numBars];

        for (Series set : dataset.series().values()) {
            Color color = set.color();

            for (Point p : set.points()) {
                int xIndex = (int) p.getX();
                double val = p.getY();

                if (val == 0) continue;

                double previousSum = categoryAccumulators[xIndex];
                double currentSum = previousSum + val;

                // Calcular Y en píxeles (bottom es 0, top es el máximo)
                int pixelYBottom = bottom - (int) ((previousSum / maxY) * graphHeight);
                int pixelYTop = bottom - (int) ((currentSum / maxY) * graphHeight);

                int centerX = left + (int) ((xIndex * stepX) + (stepX / 2));
                int startX = centerX - (int) (barWidth / 2);
                int endX = centerX + (int) (barWidth / 2);

                // El bloque va desde el Top hasta el Bottom.
                drawSolidRect(startX, pixelYTop, endX, pixelYBottom, color);

                // Actualizar la pila para la siguiente serie (ej: de macho a hembra)
                categoryAccumulators[xIndex] = currentSum;
            }
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