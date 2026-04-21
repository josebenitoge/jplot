package histogram;

import model.Pixel;
import model.Point;
import model.Series;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistogramRenderer {

    private final HistogramDataset dataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public HistogramRenderer(HistogramDataset dataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
        this.dataset = dataset;
        this.pixels = pixels;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public void bind() {
        int width = right - left;
        int height = bottom - top;
        if (width <= 0 || height <= 0) return;

        double minX = dataset.getMinX();
        double maxX = dataset.getMaxX();
        double minY = dataset.getMinY();
        double maxY = dataset.getMaxY();

        // Expansión horizontal para que las barras de los extremos no se corten por la mitad
        double xRange = maxX - minX;
        if (xRange == 0) xRange = 1;
        minX -= xRange * 0.05;
        maxX += xRange * 0.05;

        // Margen superior (10%) para que la barra más alta respire
        maxY += (maxY - minY) * 0.1;

        int baselineY = bottom; 

        for (Series baseSet : dataset.series().values()) {
            Color fillColor = baseSet.color();
            Color strokeColor = Color.BLACK;

            List<HistogramPoint> pts = new ArrayList<>();
            for (Point p : baseSet.points()) pts.add((HistogramPoint) p);
            Collections.sort(pts);

            if (pts.size() < 2) continue;

            // Calcular anchura matemática del bin
            double binWidthMath = pts.get(1).getX() - pts.get(0).getX();

            for (HistogramPoint p : pts) {
                // Cálculo de límites por barra para evitar huecos intermedios
                double leftMath = p.getX() - (binWidthMath / 2.0);
                double rightMath = p.getX() + (binWidthMath / 2.0);

                int xStart = left + (int) Math.round((leftMath - minX) / (maxX - minX) * width);
                int xEnd = left + (int) Math.round((rightMath - minX) / (maxX - minX) * width);
                int py = bottom - (int) Math.round((p.getY() - minY) / (maxY - minY) * height);

                // 1. Relleno de la barra
                for (int x = xStart; x < xEnd; x++) {
                    for (int y = py; y <= baselineY; y++) {
                        if (x >= 0 && x < pixels.length && y >= 0 && y < pixels[0].length) {
                            pixels[x][y].color = fillColor;
                        }
                    }
                }

                // 2. Contorno (Stroke)
                drawSafeLine(xStart, py, xEnd, py, strokeColor);             // Superior
                drawSafeLine(xStart, py, xStart, baselineY, strokeColor);    // Izquierda
                drawSafeLine(xEnd, py, xEnd, baselineY, strokeColor);        // Derecha
                drawSafeLine(xStart, baselineY, xEnd, baselineY, strokeColor); // Base
            }
        }
    }

    private void drawSafeLine(int x0, int y0, int x1, int y1, Color c) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy, e2;

        while (true) {
            if (x0 >= 0 && x0 < pixels.length && y0 >= 0 && y0 < pixels[0].length) {
                pixels[x0][y0].color = c;
            }
            if (x0 == x1 && y0 == y1) break;
            e2 = 2 * err;
            if (e2 >= dy) { err += dy; x0 += sx; }
            if (e2 <= dx) { err += dx; y0 += sy; }
        }
    }
}