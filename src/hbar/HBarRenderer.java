package hbar;

import model.Dataset;
import model.Pixel;
import model.Point;
import model.Series;
import java.awt.Color;
import java.util.List;

public class HBarRenderer {

    private final HBarDataset dataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public HBarRenderer(Dataset dataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
        this.dataset = (HBarDataset) dataset;
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
        int numSeries = dataset.series().size();
        if (numCategories == 0 || numSeries == 0) return;

        double minX = 0.0;
        double maxX = dataset.getMaxX();
        if (maxX == 0) maxX = 1.0;

        // Calculamos el espacio vertical para cada categoría
        double stepY = (double) graphHeight / numCategories;
        double groupHeight = stepY * 0.8; // 80% barra, 20% aire
        double barHeight = groupHeight / numSeries;

        int seriesIndex = 0;

        for (Series set : dataset.series().values()) {
            Color color = set.color();

            for (Point p : set.points()) {
                double val = p.getX();      // El valor numérico es la X
                int yIndex = (int) p.getY(); // La categoría es la Y

                // Usamos directamente yIndex en lugar de invertirlo
                double categoryCenterY = top + (yIndex * stepY) + (stepY / 2.0);

                // Calculamos dónde empieza el grupo de barras
                double groupStartY = categoryCenterY - (groupHeight / 2.0);

                // Calculamos el centro vertical de ESTA barra en concreto
                double barCenterY = groupStartY + (seriesIndex * barHeight) + (barHeight / 2.0);

                // Calculamos el ancho de la barra en píxeles
                double ratioX = (val - minX) / (maxX - minX);
                int pixelWidth = (int) (ratioX * targetWidth);

                // Coordenadas finales del rectángulo
                int startY = (int) (barCenterY - (barHeight / 2.0));
                int endY = (int) (barCenterY + (barHeight / 2.0));

                // Separador visual si hay más de 1 serie
                if (numSeries > 1) endY -= 1;

                drawSolidRect(left, startY, left + pixelWidth, endY, color);
            }
            seriesIndex++;
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