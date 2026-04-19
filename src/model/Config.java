package model;

import java.awt.*;

public abstract class Config {

    private Dataset lineDataset;
    private final String xTitle;
    private final String yTitle;
    private boolean gridH, gridV;
    private boolean crosshairH;
    private boolean crosshairV;

    public Config(String xTitle, String yTitle) {
        this.xTitle = xTitle;
        this.yTitle = yTitle;
    }

    // Recibimos el gestor principal de datos
    public void setDataset(Dataset lineDataset) {
        this.lineDataset = lineDataset;
    }

    public Dataset getDataset() {
        return lineDataset;
    }

    public void drawAxisLabels(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        if (lineDataset == null) return;

        double minX = lineDataset.getMinX();
        double maxX = lineDataset.getMaxX();
        double minY = lineDataset.getMinY();
        double maxY = lineDataset.getMaxY();

        // Evitar división por cero si solo hay un punto o todo es 0
        if (maxX == minX) {
            maxX += 1;
            minX -= 1;
        }
        if (maxY == minY) {
            maxY += 1;
            minY -= 1;
        }

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();

        // Calculamos cuántos ticks caben según la pantalla (1 cada ~60px en X, 1 cada ~40px en Y)
        int numTicksX = Math.max(1, (rightM - leftM) / 60);
        int numTicksY = Math.max(1, (bottomM - topM) / 40);

        // Eje X
        for (int i = 0; i <= numTicksX; i++) {
            int x_pos = leftM + i * (rightM - leftM) / numTicksX;
            double val = minX + i * (maxX - minX) / numTicksX;
            // Formatear a 1 decimal para acomodar números arbitrarios/negativos
            String label = String.format("%.1f", val);
            g2.drawString(label, x_pos - (fm.stringWidth(label) / 2), bottomM + 20);
        }

        // Eje Y
        for (int i = 0; i <= numTicksY; i++) {
            int y_pos = bottomM - i * (bottomM - topM) / numTicksY;
            double val = minY + i * (maxY - minY) / numTicksY;
            String label = String.format("%.1f", val);
            g2.drawString(label, leftM - fm.stringWidth(label) - 10, y_pos + 5);
        }
    }

    public void drawAxisTitles(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g2.getFontMetrics();

        // Título Eje X
        int centerX = leftM + (rightM - leftM) / 2;
        g2.drawString(xTitle, centerX - (fm.stringWidth(xTitle) / 2), bottomM + 50);

        // Título Eje Y (Rotado)
        int centerY = topM + (bottomM - topM) / 2;
        java.awt.geom.AffineTransform old = g2.getTransform();

        g2.translate(leftM - 55, centerY);
        g2.rotate(-Math.PI / 2);
        g2.drawString(yTitle, -(fm.stringWidth(yTitle) / 2), 0);

        g2.setTransform(old);
    }

    public void drawLegend(Graphics2D g2, int x, int y) {
        if (lineDataset == null || lineDataset.series().isEmpty()) return;

        // Versión simplificada para la leyenda usando los DotSet nuevos
        g2.setColor(new Color(248, 248, 248));
        g2.fillRoundRect(x, y, 120, 20 + (lineDataset.series().size() * 20), 10, 10);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRoundRect(x, y, 120, 20 + (lineDataset.series().size() * 20), 10, 10);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        int i = 0;
        for (Series set : lineDataset.series().values()) {
            g2.setColor(set.color());
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x + 10, y + 25 + (i * 20), x + 30, y + 25 + (i * 20));
            g2.setColor(Color.BLACK);
            g2.drawString(set.name(), x + 35, y + 30 + (i * 20));
            i++;
        }
    }

    public void drawHorizontalGrid(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        if (!gridH) return;
        if (lineDataset == null) return;

        int numTicksY = Math.max(1, (bottomM - topM) / 40);

        // Guardamos el trazo original para no afectar a otros dibujos
        Stroke oldStroke = g2.getStroke();

        g2.setColor(new Color(230, 230, 230)); // Gris muy clarito
        // Creamos un trazo discontinuo: 5px pintados, 5px transparentes
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
        g2.setStroke(dashed);

        // Empezamos en 1 y terminamos en numTicksY-1 para no pintar sobre los bordes negros
        for (int i = 1; i < numTicksY; i++) {
            int y_pos = bottomM - i * (bottomM - topM) / numTicksY;
            g2.drawLine(leftM, y_pos, rightM, y_pos);
        }

        g2.setStroke(oldStroke); // Restauramos el trazo original
    }

    public void drawVerticalGrid(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        if (!gridV) return;
        if (lineDataset == null) return;

        int numTicksX = Math.max(1, (rightM - leftM) / 60);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(new Color(230, 230, 230));
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
        g2.setStroke(dashed);

        for (int i = 1; i < numTicksX; i++) {
            int x_pos = leftM + i * (rightM - leftM) / numTicksX;
            g2.drawLine(x_pos, topM, x_pos, bottomM);
        }

        g2.setStroke(oldStroke);
    }

    // ¡NUEVO! Setter para controlar los ejes de origen
    public void setCrosshair(boolean showX, boolean showY) {
        this.crosshairH = showX;
        this.crosshairV = showY;
    }

    // ¡NUEVO! Método para dibujar una cruz exactamente en el centro visual del gráfico
    public void drawCrosshair(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        if (lineDataset == null) return;
        if (!crosshairH && !crosshairV) return; // Si ambos están apagados, no hacemos nada

        Stroke oldStroke = g2.getStroke();

        // Línea continua y visible (grosor 1.5)
        g2.setStroke(new BasicStroke(0.2f));

        // Gris oscuro para destacar sobre la cuadrícula
        g2.setColor(new Color(160, 160, 160, 145));

        // 1. Dibujar la línea Horizontal en la mitad exacta de la pantalla
        if (crosshairH) {
            int middleY = topM + (bottomM - topM) / 2;
            g2.drawLine(leftM, middleY, rightM, middleY);
        }

        // 2. Dibujar la línea Vertical en la mitad exacta de la pantalla
        if (crosshairV) {
            int middleX = leftM + (rightM - leftM) / 2;
            g2.drawLine(middleX, topM, middleX, bottomM);
        }

        g2.setStroke(oldStroke);
    }
    public void bindBorders(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (pixels.length == 0 || lineDataset == null) return;
        int tickSize = 6;

        // 1. Dibujar Marco Rectangular
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                boolean isVert = (i == left || i == right) && (j >= top && j <= bottom);
                boolean isHorz = (j == top || j == bottom) && (i >= left && i <= right);
                if (isVert || isHorz) pixels[i][j].color = Color.BLACK;
            }
        }

        // Mismos cálculos de divisiones que en drawAxisLabels
        int numTicksX = Math.max(1, (right - left) / 60);
        int numTicksY = Math.max(1, (bottom - top) / 40);

        // 2. Ticks en X
        for (int i = 0; i <= numTicksX; i++) {
            int x_pos = left + i * (right - left) / numTicksX;
            for (int ty = 0; ty < tickSize; ty++) {
                if (x_pos < pixels.length && (bottom + ty) < pixels[0].length) {
                    pixels[x_pos][bottom + ty].color = Color.BLACK;
                }
            }
        }

        // 3. Ticks en Y
        for (int i = 0; i <= numTicksY; i++) {
            int y_pos = bottom - i * (bottom - top) / numTicksY;
            for (int tx = 0; tx < tickSize; tx++) {
                if (y_pos < pixels[0].length && (left - tx) >= 0) {
                    pixels[left - tx][y_pos].color = Color.BLACK;
                }
            }
        }
    }

    public abstract void bind(Pixel[][] pixels, int left, int right, int top, int bottom);

    public void setGridH(boolean grid) {
        this.gridH = grid;
    }

    public void setGridV(boolean grid) {
        this.gridV = grid;
    }
}
