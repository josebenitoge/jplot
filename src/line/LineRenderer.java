package line;

import model.Pixel;
import model.Point;
import model.Series;

import java.awt.Color;

public class LineRenderer {

    private final LineDataset lineDataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public LineRenderer(LineDataset lineDataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
        this.lineDataset = lineDataset;
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

        double minX = lineDataset.getMinX();
        double maxX = lineDataset.getMaxX();
        double minY = lineDataset.getMinY();
        double maxY = lineDataset.getMaxY();

        if (maxX == minX) { maxX += 1; minX -= 1; }
        if (maxY == minY) { maxY += 1; minY -= 1; }

        for (Series set : lineDataset.series().values()) {
            Color baseColor = set.color();
            String style = ((LineSeries) set).getStyle();
            boolean isFunction = LineDataset.TYPE_FUNCTION.equals(((LineSeries) set).getType());

            // 1. MÁSCARA GLOBAL (Evita los espacios en blanco en las uniones de los segmentos)
            double[][] coverageMap = new double[pixels.length][pixels[0].length];

            int prevX = -1;
            int prevY = -1;
            double accumulatedDistance = 0;

            for (Point mathDataPoint : set.points()) {

                double ratioX = (mathDataPoint.getX() - minX) / (maxX - minX);
                double ratioY = (mathDataPoint.getY() - minY) / (maxY - minY);

                int pixelX = left + (int) Math.round(ratioX * targetWidth);
                int pixelY = bottom - (int) Math.round(ratioY * graphHeight);

                if (isFunction) {
                    if (prevX != -1) {
                        double segmentLength = Math.sqrt(Math.pow(pixelX - prevX, 2) + Math.pow(pixelY - prevY, 2));
                        accumulatedDistance += segmentLength;

                        // Un grosor de 2.2 ahora se verá sólido y contundente
                        double lineThickness = 2.2;

                        if (LineDataset.STYLE_DASHED.equals(style)) {
                            if (accumulatedDistance % 20 < 10) {
                                drawAntiAliasedLine(prevX, prevY, pixelX, pixelY, lineThickness, coverageMap);
                            }
                        } else {
                            drawAntiAliasedLine(prevX, prevY, pixelX, pixelY, lineThickness, coverageMap);
                        }
                    }
                    prevX = pixelX;
                    prevY = pixelY;
                } else {
                    double size = 10.0;
                    double circleSize = 12.5;

                    if (LineDataset.STYLE_CROSS.equals(style)) {
                        drawPerfectCross(pixelX, pixelY, size, coverageMap);
                    } else if (LineDataset.STYLE_TRIANGLE.equals(style)) {
                        drawPerfectTriangle(pixelX, pixelY, size, coverageMap);
                    } else {
                        drawAntiAliasedPoint(pixelX, pixelY, circleSize, coverageMap);
                    }
                }
            }

            // 2. VOLCADO AL LIENZO REAL (Solo pintamos una vez por píxel)
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

    private void drawPerfectCross(int cx, int cy, double size, double[][] map) {
        int r = (int) (size / 2);
        drawAntiAliasedLine(cx - r, cy - r, cx + r, cy + r, 2.0, map);
        drawAntiAliasedLine(cx - r, cy + r, cx + r, cy - r, 2.0, map);
    }

    private void drawPerfectTriangle(int cx, int cy, double size, double[][] map) {
        int r = (int) (size / 2);
        int topX = cx, topY = cy - r;
        int leftX = cx - r, leftY = cy + r;
        int rightX = cx + r, rightY = cy + r;

        drawAntiAliasedLine(topX, topY, leftX, leftY, 2.0, map);
        drawAntiAliasedLine(leftX, leftY, rightX, rightY, 2.0, map);
        drawAntiAliasedLine(rightX, rightY, topX, topY, 2.0, map);
    }

    private void drawAntiAliasedPoint(int cx, int cy, double diameter, double[][] coverageMap) {
        double radius = diameter / 2.0;

        int minX = (int) Math.max(left + 1, Math.floor(cx - radius - 1.5));
        int maxX = (int) Math.min(right - 1, Math.ceil(cx + radius + 1.5));
        int minY = (int) Math.max(top + 1, Math.floor(cy - radius - 1.5));
        int maxY = (int) Math.min(bottom - 1, Math.ceil(cy + radius + 1.5));

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                // Validación para no salirnos del array
                if (px < 0 || py < 0 || px >= coverageMap.length || py >= coverageMap[0].length) continue;

                double dist = Math.hypot(px - cx, py - cy);

                // SDF Lineal: 100% sólido hasta radius-0.5, luego degradado lineal de 1 px
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
                // Validación para no salirnos de los márgenes de la memoria
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

                // La magia de SDF: el núcleo es inquebrantable, y la caída es perfecta
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

        int outR = (int) Math.round(
                (src.getRed() * srcA + dst.getRed() * dstA * (1.0 - srcA)) / outA
        );
        int outG = (int) Math.round(
                (src.getGreen() * srcA + dst.getGreen() * dstA * (1.0 - srcA)) / outA
        );
        int outB = (int) Math.round(
                (src.getBlue() * srcA + dst.getBlue() * dstA * (1.0 - srcA)) / outA
        );

        int outAlpha = (int) Math.round(outA * 255.0);

        return new Color(
                clamp(outR),
                clamp(outG),
                clamp(outB),
                clamp(outAlpha)
        );
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}