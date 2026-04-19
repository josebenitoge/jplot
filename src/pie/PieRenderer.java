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
        // 1. Calcular el total de todas las porciones
        double total = 0;
        for (Series s : dataset.series().values()) {
            for (Point p : s.points()) total += p.getY();
        }
        if (total == 0) return;

        // 2. Definir centro y radio
        int centerX = left + (right - left) / 2;
        int centerY = top + (bottom - top) / 2;
        int radius = Math.min(right - left, bottom - top) / 3;

        // 3. Renderizado por píxel (Barrido radial)
        double currentAngle = 0;
        for (Series s : dataset.series().values()) {
            double value = 0;
            for (Point p : s.points()) value += p.getY();
            
            double sliceAngle = (value / total) * 2 * Math.PI;
            drawSlice(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle, s.color());
            currentAngle += sliceAngle;
        }
    }

    private void drawSlice(int cx, int cy, int r, double startAng, double endAng, Color color) {
        for (int x = cx - r; x <= cx + r; x++) {
            for (int y = cy - r; y <= cy + r; y++) {
                if (x < 0 || x >= pixels.length || y < 0 || y >= pixels[0].length) continue;

                double dx = x - cx;
                double dy = cy - y; // Invertir Y para coordenadas estándar
                double dist = Math.sqrt(dx * dx + dy * dy);
                
                if (dist <= r) {
                    double angle = Math.atan2(dy, dx);
                    if (angle < 0) angle += 2 * Math.PI;
                    
                    // Ajustar para que el 0 esté arriba (norte)
                    double normStart = (Math.PI / 2 - endAng);
                    double normEnd = (Math.PI / 2 - startAng);
                    
                    // Lógica de pertenencia al sector circular
                    if (isAngleBetween(angle, startAng, endAng)) {
                        // Antialiasing básico en el borde del radio
                        if (dist > r - 1) {
                            double alpha = (r - dist);
                            pixels[x][y].color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * alpha));
                        } else {
                            pixels[x][y].color = color;
                        }
                    }
                }
            }
        }
    }

    private boolean isAngleBetween(double angle, double start, double end) {
        // Normalizar ángulos para evitar problemas de salto de 2PI
        double a = (angle + Math.PI / 2) % (2 * Math.PI);
        if (a < 0) a += 2 * Math.PI;
        return a >= start && a <= end;
    }
}