package stem;

import model.Pixel;
import model.Point;
import model.Series;

import java.awt.Color;

public class StemRenderer {

    private final StemDataset stemDataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public StemRenderer(StemDataset stemDataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
        this.stemDataset = stemDataset;
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

        double minX = stemDataset.getMinX();
        double maxX = stemDataset.getMaxX();
        double minY = stemDataset.getMinY();
        double maxY = stemDataset.getMaxY();

        if (maxX == minX) { maxX += 1; minX -= 1; }
        if (maxY == minY) { maxY += 1; minY -= 1; }

        // 1. CÁLCULO DE LA LÍNEA BASE (Y=0)
        int baselineY = bottom;
        if (minY <= 0 && maxY >= 0) {
            baselineY = bottom - (int) Math.round((0.0 - minY) / (maxY - minY) * graphHeight);
        } else if (minY > 0) {
            baselineY = bottom;
        } else {
            baselineY = top;
        }

        // 2. DIBUJAR LÍNEA BASE GLOBAl (Directo al lienzo)
        double[][] baseMap = new double[pixels.length][pixels[0].length];
        drawAntiAliasedLine(left, baselineY, right, baselineY, 1.5, baseMap);
        Color baselineColor = new Color(200, 0, 0); // Rojo clásico para Y=0
        for (int px = 0; px < baseMap.length; px++) {
            for (int py = 0; py < baseMap[0].length; py++) {
                if (baseMap[px][py] > 0) putAntialiasedPixel(px, py, baselineColor, baseMap[px][py]);
            }
        }

        // 3. RENDERIZADO DE SERIES POR MAPA DE COBERTURA (Evita auto-superposición)
        for (Series set : stemDataset.series().values()) {
            Color baseColor = set.color();
            String style = ((StemSeries) set).getStyle();
            
            double[][] coverageMap = new double[pixels.length][pixels[0].length];

            for (Point mathDataPoint : set.points()) {
                double ratioX = (mathDataPoint.getX() - minX) / (maxX - minX);
                double ratioY = (mathDataPoint.getY() - minY) / (maxY - minY);

                int pixelX = left + (int) Math.round(ratioX * targetWidth);
                int pixelY = bottom - (int) Math.round(ratioY * graphHeight);

                // Tallo (Stem)
                drawAntiAliasedLine(pixelX, baselineY, pixelX, pixelY, 2.2, coverageMap);

                // Marcador (Marker)
                double diameter = 12.0;
                boolean hollow = style.contains("HOLLOW");
                
                if (style.contains("SQUARE")) {
                    drawSDFShape(pixelX, pixelY, diameter, hollow, "SQUARE", coverageMap);
                } else if (style.contains("LOZENGE")) {
                    drawSDFShape(pixelX, pixelY, diameter, hollow, "LOZENGE", coverageMap);
                } else {
                    drawSDFShape(pixelX, pixelY, diameter, hollow, "CIRCLE", coverageMap);
                }
            }

            // VOLCADO AL LIENZO REAL
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
    // SDF (Signed Distance Field) PARA FIGURAS PERFECTAS
    // =========================================================

    private void drawSDFShape(int cx, int cy, double diameter, boolean hollow, String shapeType, double[][] coverageMap) {
        double radius = diameter / 2.0;
        double stroke = 2.2; // Grosor de línea para figuras huecas

        int minX = (int) Math.max(left + 1, Math.floor(cx - radius - 2));
        int maxX = (int) Math.min(right - 1, Math.ceil(cx + radius + 2));
        int minY = (int) Math.max(top + 1, Math.floor(cy - radius - 2));
        int maxY = (int) Math.min(bottom - 1, Math.ceil(cy + radius + 2));

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                if (px < 0 || py < 0 || px >= coverageMap.length || py >= coverageMap[0].length) continue;

                double dx = px - cx;
                double dy = py - cy;
                double dist = 0;

                // 1. Calcular la distancia al centro dependiendo de la geometría matemática
                if ("CIRCLE".equals(shapeType)) {
                    dist = Math.hypot(dx, dy);
                } else if ("SQUARE".equals(shapeType)) {
                    dist = Math.max(Math.abs(dx), Math.abs(dy));
                } else if ("LOZENGE".equals(shapeType)) {
                    dist = (Math.abs(dx) + Math.abs(dy)) * 0.70710678; // Ajuste diagonal para SDF correcto
                }

                // 2. Evaluar el campo de distancia (Sólido vs Hueco)
                double coverage;
                if (hollow) {
                    double midStroke = radius - (stroke / 2.0);
                    double distToStroke = Math.abs(dist - midStroke);
                    coverage = Math.max(0.0, Math.min(1.0, (stroke / 2.0) - distToStroke + 0.5));
                } else {
                    coverage = Math.max(0.0, Math.min(1.0, radius - dist + 0.5));
                }

                // 3. Imprimir en la máscara global de la serie
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
    // UTILIDADES DE MEZCLA
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