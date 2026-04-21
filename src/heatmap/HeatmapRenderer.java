package heatmap;

import model.Dataset;
import model.Pixel;
import model.Point;
import model.Series;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class HeatmapRenderer {

    private static final double PLATEAU_THRESHOLD = 0.5;
    private static final double STEEPNESS = 7.0;

    private final Dataset dataset;
    private final Pixel[][] pixels;
    private final int left, right, top, bottom;

    public HeatmapRenderer(Dataset dataset, Pixel[][] pixels, int left, int right, int top, int bottom) {
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

        if (maxX == minX) { maxX += 1; minX -= 1; }
        if (maxY == minY) { maxY += 1; minY -= 1; }

        List<Node> nodes = new ArrayList<>();
        double maxZ = Double.NEGATIVE_INFINITY;

        // 1. Proyectar nodos y buscar el Z máximo global
        for (Series set : dataset.series().values()) {
            Color color = set.color();
            for (Point basePoint : set.points()) {
                if (basePoint instanceof HeatmapPoint p) {
                    double rx = (p.getX() - minX) / (maxX - minX);
                    double ry = (p.getY() - minY) / (maxY - minY);
                    int px = left + (int) (rx * targetWidth);
                    int py = bottom - (int) (ry * graphHeight);
                    maxZ = Math.max(maxZ, p.getZ());
                    nodes.add(new Node(px, py, p.getZ(), color));
                }
            }
        }

        if (nodes.isEmpty()) return;
        if (maxZ <= 0) maxZ = 1.0;

        // 2. Configurar radios libres y buscar cuadrantes
        for (Node n : nodes) {
            // Asignamos un radio libre máximo (ej. un tercio de la pantalla) escalado por Z
            n.setFreeRadius(maxZ, targetWidth, graphHeight);
            n.findQuadrants(nodes);
        }

        // 3. Renderizado
        for (int x = left; x < right; x++) {
            for (int y = top; y < bottom; y++) {
                if (x < 0 || x >= pixels.length || y < 0 || y >= pixels[0].length) continue;

                double totalWeight = 0;
                double rSum = 0, gSum = 0, bSum = 0;
                double maxAlphaFound = 0;

                for (Node n : nodes) {
                    double influence = n.influenceAt(x, y);
                    if (influence <= 0) continue;

                    // CAMBIO CLAVE: La opacidad/peso es pura influencia (máximo 1.0 en el centro).
                    // Ya no se reduce por culpa del valor Z.
                    double weight = influence;

                    rSum += n.color.getRed() * weight;
                    gSum += n.color.getGreen() * weight;
                    bSum += n.color.getBlue() * weight;

                    totalWeight += weight;

                    if (weight > maxAlphaFound) maxAlphaFound = weight;
                }

                if (totalWeight > 0) {
                    int rMix = clamp255((int) (rSum / totalWeight));
                    int gMix = clamp255((int) (gSum / totalWeight));
                    int bMix = clamp255((int) (bSum / totalWeight));

                    Pixel dst = pixels[x][y];
                    double alpha = maxAlphaFound; // La opacidad final será 1.0 en el núcleo

                    int outR = clamp255((int) (rMix * alpha + dst.color.getRed() * (1 - alpha)));
                    int outG = clamp255((int) (gMix * alpha + dst.color.getGreen() * (1 - alpha)));
                    int outB = clamp255((int) (bMix * alpha + dst.color.getBlue() * (1 - alpha)));

                    pixels[x][y].color = new Color(outR, outG, outB);
                }
            }
        }
    }

    private static class Node {
        int x, y;
        double z;
        Color color;
        Node nw, ne, sw, se;
        double freeRadius;

        Node(int x, int y, double z, Color color) {
            this.x = x; this.y = y; this.z = z; this.color = color;
        }

        /**
         * Calcula hasta dónde llega el nodo si no tiene vecinos molestando.
         */
        void setFreeRadius(double maxZ, int targetWidth, int targetHeight) {
            double globalBaseRadius = Math.max(targetWidth, targetHeight) / 3.0;
            this.freeRadius = globalBaseRadius * (this.z / maxZ);
        }

        void findQuadrants(List<Node> nodes) {
            double bNW = Double.MAX_VALUE, bNE = Double.MAX_VALUE, bSW = Double.MAX_VALUE, bSE = Double.MAX_VALUE;
            for (Node o : nodes) {
                if (o == this) continue;
                double dx = o.x - x, dy = o.y - y, d = dx * dx + dy * dy;
                if (dx <= 0 && dy <= 0 && d < bNW) { bNW = d; nw = o; }
                if (dx >= 0 && dy <= 0 && d < bNE) { bNE = d; ne = o; }
                if (dx <= 0 && dy >= 0 && d < bSW) { bSW = d; sw = o; }
                if (dx >= 0 && dy >= 0 && d < bSE) { bSE = d; se = o; }
            }
        }

        double influenceAt(int px, int py) {
            double v = 0;
            v = Math.max(v, influence(px, py, nw));
            v = Math.max(v, influence(px, py, ne));
            v = Math.max(v, influence(px, py, sw));
            v = Math.max(v, influence(px, py, se));
            return v;
        }

        double influence(int px, int py, Node n) {
            double d = Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2));
            double maxD;

            if (n == null) {
                // CAMBIO CLAVE: Si no hay vecino, el espacio ocupado es su "freeRadius" (basado en Z)
                maxD = this.freeRadius;
            } else {
                double totalD = Math.sqrt(Math.pow(x - n.x, 2) + Math.pow(y - n.y, 2));
                // CAMBIO CLAVE: La frontera ya no está en la mitad.
                // El nodo con más Z empuja la frontera más lejos.
                maxD = totalD * (this.z / (this.z + n.z));
            }

            if (maxD <= 0 || d > maxD) return 0;
            return 1.0 / (1.0 + Math.pow((d / maxD) / PLATEAU_THRESHOLD, STEEPNESS));
        }
    }

    private int clamp255(int v) {
        return Math.max(0, Math.min(255, v));
    }
}