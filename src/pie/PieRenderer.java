package pie;

import model.Dataset;
import model.Pixel;
import model.Point;
import model.Series;
import java.awt.Color;

public class PieRenderer {

    private final Dataset dataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public PieRenderer(Dataset dataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
        this.dataset = dataset;
        this.pixels = pixels;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public void bind() {
        double total = 0;
        for (Series s : dataset.series().values()) {
            for (Point p : s.points()) total += p.getY();
        }
        if (total <= 0) return;

        int centerX = left + (right - left) / 2;
        int centerY = top + (bottom - top) / 2;
        int radius = Math.min(right - left, bottom - top) / 3;

        // En pantallas, las 12 en punto (Norte) corresponde a -PI/2 porque Y crece hacia abajo.
        double currentAngle = -Math.PI / 2;

        for (Series s : dataset.series().values()) {
            double value = 0;
            for (Point p : s.points()) value += p.getY();

            if (value <= 0) continue;

            double sliceAngle = (value / total) * 2 * Math.PI;

            // Si una serie ocupa el 100%, dibujamos un círculo completo para evitar colapsos matemáticos
            if (sliceAngle >= 2 * Math.PI - 0.001) {
                drawSDFCircle(centerX, centerY, radius, s.color());
            } else {
                drawSDFSlice(centerX, centerY, radius, currentAngle, sliceAngle, s.color());
            }

            currentAngle += sliceAngle;
        }
    }

    private void drawSDFSlice(int cx, int cy, double r, double startAng, double sliceAngle, Color color) {
        double endAng = startAng + sliceAngle;

        // 1. Vectores normales apuntando hacia el interior de la porción
        // Medio-plano 1 (Ángulo de inicio, avanza en sentido horario)
        double n1X = -Math.sin(startAng);
        double n1Y = Math.cos(startAng);

        // Medio-plano 2 (Ángulo de fin, retrocede en sentido antihorario)
        double n2X = Math.sin(endAng);
        double n2Y = -Math.cos(endAng);

        boolean isLargeArc = sliceAngle >= Math.PI;

        // 2. Optimización: Solo iterar sobre el bounding box del círculo
        int minX = (int) Math.max(left, cx - r - 2);
        int maxX = (int) Math.min(right - 1, cx + r + 2);
        int minY = (int) Math.max(top, cy - r - 2);
        int maxY = (int) Math.min(bottom - 1, cy + r + 2);

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                double dx = px - cx;
                double dy = py - cy;

                // A. Distancia al arco exterior (Círculo)
                double distCircle = r - Math.hypot(dx, dy);
                double covCircle = Math.max(0.0, Math.min(1.0, distCircle + 0.5));

                if (covCircle <= 0) continue; // Si está fuera del círculo, descartamos rápido

                // B. Distancias a los cortes rectos
                double d1 = dx * n1X + dy * n1Y;
                double d2 = dx * n2X + dy * n2Y;

                double cov1 = Math.max(0.0, Math.min(1.0, d1 + 0.5));
                double cov2 = Math.max(0.0, Math.min(1.0, d2 + 0.5));

                // C. Intersección Booleana Suave
                double covAngle;
                if (isLargeArc) {
                    covAngle = Math.max(cov1, cov2); // Unión para ángulos obtusos (> 180º)
                } else {
                    covAngle = Math.min(cov1, cov2); // Intersección para ángulos agudos (< 180º)
                }

                // D. Cobertura final del píxel
                double finalCoverage = Math.min(covCircle, covAngle);

                if (finalCoverage > 0) {
                    putAntialiasedPixel(px, py, color, finalCoverage);
                }
            }
        }
    }

    private void drawSDFCircle(int cx, int cy, double r, Color color) {
        int minX = (int) Math.max(left, cx - r - 2);
        int maxX = (int) Math.min(right - 1, cx + r + 2);
        int minY = (int) Math.max(top, cy - r - 2);
        int maxY = (int) Math.min(bottom - 1, cy + r + 2);

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                double distCircle = r - Math.hypot(px - cx, py - cy);
                double coverage = Math.max(0.0, Math.min(1.0, distCircle + 0.5));
                if (coverage > 0) {
                    putAntialiasedPixel(px, py, color, coverage);
                }
            }
        }
    }

    // =========================================================
    // MOTOR DE MEZCLA (ALPHA BLENDING)
    // =========================================================

    private void putAntialiasedPixel(int x, int y, Color src, double coverage) {
        if (x < 0 || y < 0 || x >= pixels.length || y >= pixels[0].length) return;

        coverage = Math.max(0.0, Math.min(1.0, coverage));
        int srcAlpha = (int) Math.round(src.getAlpha() * coverage);
        if (srcAlpha <= 0) return;

        Pixel currentPixel = pixels[x][y];
        Color dst = currentPixel.color;

        double srcA = srcAlpha / 255.0;
        double dstA = dst.getAlpha() / 255.0;

        double outA = srcA + dstA * (1.0 - srcA);
        if (outA <= 0.0) return;

        int outR = (int) Math.round((src.getRed() * srcA + dst.getRed() * dstA * (1.0 - srcA)) / outA);
        int outG = (int) Math.round((src.getGreen() * srcA + dst.getGreen() * dstA * (1.0 - srcA)) / outA);
        int outB = (int) Math.round((src.getBlue() * srcA + dst.getBlue() * dstA * (1.0 - srcA)) / outA);

        currentPixel.color = new Color(clamp(outR), clamp(outG), clamp(outB), clamp((int)(outA * 255)));
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}