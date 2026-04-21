package polyline;

import model.Pixel;
import model.Point;
import model.Series;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolylineRenderer {

    private final PolylineDataset dataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public PolylineRenderer(PolylineDataset dataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
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

        if (maxX == minX) { maxX += 1; minX -= 1; }

        // Margen en Y (10%) para evitar que los picos toquen el borde
        double marginY = (maxY - minY) * 0.1;
        if (maxY == minY) { maxY += 1; minY -= 1; }
        else { maxY += marginY; minY -= marginY; }

        for (Series baseSet : dataset.series().values()) {
            PolylineSeries set = (PolylineSeries) baseSet;
            Color baseColor = set.color();

            List<PolylinePoint> sortedPoints = new ArrayList<>();
            for(Point p : set.points()) sortedPoints.add((PolylinePoint) p);
            Collections.sort(sortedPoints);

            if (sortedPoints.isEmpty()) continue;

            double lineThickness = 2.5;
            double markerDiameter = 12.0;

            // 1. MÁSCARA GLOBAL DE LA SERIE (Evita superposiciones oscuras)
            double[][] coverageMap = new double[pixels.length][pixels[0].length];

            int prevPx = -1, prevPy = -1;

            // 2. Trazar las líneas (SDF)
            for (PolylinePoint p : sortedPoints) {
                int px = left + (int) Math.round((p.getX() - minX) / (maxX - minX) * width);
                int py = bottom - (int) Math.round((p.getY() - minY) / (maxY - minY) * height);

                if (prevPx != -1) {
                    drawAntiAliasedLine(prevPx, prevPy, px, py, lineThickness, coverageMap);
                }
                prevPx = px;
                prevPy = py;
            }

            // 3. Trazar los marcadores (SDF) encima de las líneas
            if (!PolylineSeries.MARKER_NONE.equals(set.getMarkerStyle())) {
                for (PolylinePoint p : sortedPoints) {
                    int px = left + (int) Math.round((p.getX() - minX) / (maxX - minX) * width);
                    int py = bottom - (int) Math.round((p.getY() - minY) / (maxY - minY) * height);
                    drawAntiAliasedPoint(px, py, markerDiameter, coverageMap);
                }
            }

            // 4. VOLCADO AL LIENZO REAL (Alpha Blending)
            for (int px = 0; px < coverageMap.length; px++) {
                for (int py = 0; py < coverageMap[0].length; py++) {
                    if (coverageMap[px][py] > 0) {
                        putAntialiasedPixel(px, py, baseColor, coverageMap[px][py]);
                    }
                }
            }
        }
    }

    // =========================================================
    // FORMAS CON ANTIALIASING (SDF Lineal)
    // =========================================================

    private void drawAntiAliasedPoint(int cx, int cy, double diameter, double[][] coverageMap) {
        double radius = diameter / 2.0;

        int minX = (int) Math.max(left + 1, Math.floor(cx - radius - 1.5));
        int maxX = (int) Math.min(right - 1, Math.ceil(cx + radius + 1.5));
        int minY = (int) Math.max(top + 1, Math.floor(cy - radius - 1.5));
        int maxY = (int) Math.min(bottom - 1, Math.ceil(cy + radius + 1.5));

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                if (px < 0 || py < 0 || px >= coverageMap.length || py >= coverageMap[0].length) continue;

                double dist = Math.hypot(px - cx, py - cy);

                // SDF: 100% sólido hasta radius-0.5, degradado de 1px en el borde
                double coverage = Math.max(0.0, Math.min(1.0, radius - dist + 0.5));

                if (coverage > coverageMap[px][py]) {
                    coverageMap[px][py] = coverage;
                }
            }
        }
    }

    private void drawAntiAliasedLine(int x0, int y0, int x1, int y1, double thickness, double[][] coverageMap) {
        double radius = thickness / 2.0;

        int minX = (int) Math.max(left + 1, Math.floor(Math.min(x0, x1) - radius - 1.5));
        int maxX = (int) Math.min(right - 1, Math.ceil(Math.max(x0, x1) + radius + 1.5));
        int minY = (int) Math.max(top + 1, Math.floor(Math.min(y0, y1) - radius - 1.5));
        int maxY = (int) Math.min(bottom - 1, Math.ceil(Math.max(y0, y1) + radius + 1.5));

        double dx = x1 - x0;
        double dy = y1 - y0;
        double lenSq = dx * dx + dy * dy;

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                if (px < 0 || py < 0 || px >= coverageMap.length || py >= coverageMap[0].length) continue;

                double dist;

                if (lenSq == 0) {
                    dist = Math.hypot(px - x0, py - y0);
                } else {
                    double t = ((px - x0) * dx + (py - y0) * dy) / lenSq;
                    t = Math.max(0, Math.min(1, t));
                    double projX = x0 + t * dx;
                    double projY = y0 + t * dy;
                    dist = Math.hypot(px - projX, py - projY);
                }

                double coverage = Math.max(0.0, Math.min(1.0, radius - dist + 0.5));

                if (coverage > coverageMap[px][py]) {
                    coverageMap[px][py] = coverage;
                }
            }
        }
    }

    // =========================================================
    // UTILIDADES DE MEZCLA (Alpha Blending)
    // =========================================================

    private void putAntialiasedPixel(int x, int y, Color src, double coverage) {
        if (x < 0 || y < 0 || x >= pixels.length || y >= pixels[0].length) return;

        coverage = Math.max(0.0, Math.min(1.0, coverage));
        int srcAlpha = (int) Math.round(src.getAlpha() * coverage);
        if (srcAlpha <= 0) return;

        Pixel currentPixel = pixels[x][y];
        Color dst = currentPixel.color;

        Color out = alphaBlend(dst, new Color(src.getRed(), src.getGreen(), src.getBlue(), srcAlpha));
        currentPixel.color = out;
    }

    private Color alphaBlend(Color dst, Color src) {
        double srcA = src.getAlpha() / 255.0;
        double dstA = dst.getAlpha() / 255.0;

        double outA = srcA + dstA * (1.0 - srcA);
        if (outA <= 0.0) return new Color(0, 0, 0, 0);

        int outR = (int) Math.round((src.getRed() * srcA + dst.getRed() * dstA * (1.0 - srcA)) / outA);
        int outG = (int) Math.round((src.getGreen() * srcA + dst.getGreen() * dstA * (1.0 - srcA)) / outA);
        int outB = (int) Math.round((src.getBlue() * srcA + dst.getBlue() * dstA * (1.0 - srcA)) / outA);

        int outAlpha = (int) Math.round(outA * 255.0);

        return new Color(clamp(outR), clamp(outG), clamp(outB), clamp(outAlpha));
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}