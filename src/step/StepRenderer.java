package step;

import model.Pixel;
import model.Point;
import model.Series;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StepRenderer {

    private final StepDataset dataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public StepRenderer(StepDataset dataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
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
        
        // Damos margen superior e inferior
        double marginY = (maxY - minY) * 0.05;
        if (maxY == minY) { maxY += 1; minY -= 1; } 
        else { maxY += marginY; minY -= marginY; }

        for (Series baseSet : dataset.series().values()) {
            StepSeries set = (StepSeries) baseSet;
            Color baseColor = set.color();
            
            List<StepPoint> sortedPoints = new ArrayList<>();
            for (Point p : set.points()) sortedPoints.add((StepPoint) p);
            Collections.sort(sortedPoints);

            if (sortedPoints.size() < 2) continue;

            double lineThickness = 2.0;
            double[][] coverageMap = new double[pixels.length][pixels[0].length];

            for (int i = 1; i < sortedPoints.size(); i++) {
                StepPoint p0 = sortedPoints.get(i - 1);
                StepPoint p1 = sortedPoints.get(i);

                int x0 = left + (int) Math.round((p0.getX() - minX) / (maxX - minX) * width);
                int y0 = bottom - (int) Math.round((p0.getY() - minY) / (maxY - minY) * height);
                
                int x1 = left + (int) Math.round((p1.getX() - minX) / (maxX - minX) * width);
                int y1 = bottom - (int) Math.round((p1.getY() - minY) / (maxY - minY) * height);

                // LÓGICA DE ESCALÓN (POST-STEP)
                // 1. Línea Horizontal (Mantiene Y0 hasta llegar a X1)
                drawAntiAliasedLine(x0, y0, x1, y0, lineThickness, coverageMap);
                
                // 2. Línea Vertical (Salta desde Y0 hasta Y1 en X1)
                drawAntiAliasedLine(x1, y0, x1, y1, lineThickness, coverageMap);
            }

            // VOLCADO AL LIENZO REAL (Alpha Blending SDF)
            for (int px = 0; px < coverageMap.length; px++) {
                for (int py = 0; py < coverageMap[0].length; py++) {
                    if (coverageMap[px][py] > 0) {
                        putAntialiasedPixel(px, py, baseColor, coverageMap[px][py]);
                    }
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

        double dx = x1 - x0, dy = y1 - y0;
        double lenSq = dx * dx + dy * dy;

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                if (px < 0 || py < 0 || px >= coverageMap.length || py >= coverageMap[0].length) continue;

                double dist;
                if (lenSq == 0) {
                    dist = Math.hypot(px - x0, py - y0);
                } else {
                    double t = Math.max(0, Math.min(1, ((px - x0) * dx + (py - y0) * dy) / lenSq));
                    dist = Math.hypot(px - (x0 + t * dx), py - (y0 + t * dy));
                }

                double coverage = Math.max(0.0, Math.min(1.0, radius - dist + 0.5));
                if (coverage > coverageMap[px][py]) coverageMap[px][py] = coverage;
            }
        }
    }

    private void putAntialiasedPixel(int x, int y, Color src, double coverage) {
        if (x < 0 || y < 0 || x >= pixels.length || y >= pixels[0].length) return;
        int srcAlpha = (int) Math.round(src.getAlpha() * coverage);
        if (srcAlpha <= 0) return;

        Color dst = pixels[x][y].color;
        double srcA = srcAlpha / 255.0, dstA = dst.getAlpha() / 255.0;
        double outA = srcA + dstA * (1.0 - srcA);
        if (outA <= 0.0) return;

        int outR = (int) Math.round((src.getRed() * srcA + dst.getRed() * dstA * (1.0 - srcA)) / outA);
        int outG = (int) Math.round((src.getGreen() * srcA + dst.getGreen() * dstA * (1.0 - srcA)) / outA);
        int outB = (int) Math.round((src.getBlue() * srcA + dst.getBlue() * dstA * (1.0 - srcA)) / outA);

        pixels[x][y].color = new Color(outR, outG, outB);
    }
}