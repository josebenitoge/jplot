package bubble;

import model.Dataset;
import model.Pixel;
import model.Point;
import model.Series;

import java.awt.Color;

public class BubbleRenderer {

    private final Dataset dataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public BubbleRenderer(Dataset dataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
        this.dataset = dataset;
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

        double minX = dataset.getMinX();
        double maxX = dataset.getMaxX();
        double minY = dataset.getMinY();
        double maxY = dataset.getMaxY();

        // Evitar división por cero si solo hay un punto o todo está en el mismo eje
        if (maxX == minX) { maxX += 1; minX -= 1; }
        if (maxY == minY) { maxY += 1; minY -= 1; }

        // Recorremos todas las series guardadas en el Dataset
        for (Series set : dataset.series().values()) {
            Color color = set.color();

            // Recorremos los puntos genéricos de la serie
            for (Point basePoint : set.points()) {

                // Hacemos cast a BubblePoint para acceder a X, Y y Z
                if (basePoint instanceof BubblePoint) {
                    BubblePoint dot = (BubblePoint) basePoint;

                    // Proyección matemática a la pantalla
                    double ratioX = (dot.getX() - minX) / (maxX - minX);
                    double ratioY = (dot.getY() - minY) / (maxY - minY);

                    int pixelX = left + (int) (ratioX * targetWidth);
                    int pixelY = bottom - (int) (ratioY * graphHeight);

                    // Dibujamos usando la Z como diámetro
                    drawPerfectBubble(pixelX, pixelY, color, dot.getZ());
                }
            }
        }
    }

    // =========================================================
    // DIBUJO CON ANTIALIASING EXACTO
    // =========================================================
    private void drawPerfectBubble(int cx, int cy, Color baseColor, double diameter) {
        double radius = diameter / 2.0;

        int minX = (int) Math.max(left + 1, cx - Math.ceil(radius) - 1);
        int maxX = (int) Math.min(right - 1, cx + Math.ceil(radius) + 1);
        int minY = (int) Math.max(top + 1, cy - Math.ceil(radius) - 1);
        int maxY = (int) Math.min(bottom - 1, cy + Math.ceil(radius) + 1);

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {

                // Evitamos salirnos de los límites de la matriz de píxeles
                if (px < 0 || px >= pixels.length || py < 0 || py >= pixels[0].length) continue;

                double dist = Math.sqrt(Math.pow(px - cx, 2) + Math.pow(py - cy, 2));

                if (dist <= radius + 0.5) {
                    double alphaMultiplier = 1.0;
                    if (dist > radius - 0.5) alphaMultiplier = (radius + 0.5) - dist;

                    int finalAlpha = (int) (baseColor.getAlpha() * alphaMultiplier);
                    if (finalAlpha > 0) {
                        Color mixColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), finalAlpha);
                        Pixel currentPixel = pixels[px][py];

                        // Si el píxel es blanco o la nueva burbuja tiene más opacidad, lo pintamos
                        if (currentPixel.color.equals(Color.WHITE) || finalAlpha > currentPixel.color.getAlpha()) {
                            currentPixel.color = mixColor;
                        }
                    }
                }
            }
        }
    }
}