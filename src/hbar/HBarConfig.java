package hbar;

import model.Config;
import model.Pixel;
import java.awt.*;
import java.util.List;

public class HBarConfig extends Config {

    public HBarConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (getDataset() != null) {
            new HBarRenderer(getDataset(), pixels, left, right, top, bottom).bind();
        }
    }

    @Override
    public void drawAxisLabels(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        HBarDataset hbData = (HBarDataset) getDataset();
        if (hbData == null) return;

        List<String> categories = hbData.getCategories();
        double minX = 0.0; // Empezamos siempre en 0
        double maxX = hbData.getMaxX();
        if (maxX == 0) maxX = 1.0;

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();

        // 1. Dibujar Eje X (Números)
        int numTicksX = Math.max(1, (rightM - leftM) / 60);
        for (int i = 0; i <= numTicksX; i++) {
            int x_pos = leftM + i * (rightM - leftM) / numTicksX;
            double val = minX + i * (maxX - minX) / numTicksX;

            // Para que quede limpio, lo casteamos a entero si no tiene decimales
            String label = (val == Math.floor(val)) ? String.format("%.0f", val) : String.format("%.1f", val);
            g2.drawString(label, x_pos - (fm.stringWidth(label) / 2), bottomM + 20);
        }

        // 2. Dibujar Eje Y (Categorías) - De arriba a abajo en orden de inserción
        int numBars = categories.size();
        if (numBars > 0) {
            double stepY = (double) (bottomM - topM) / numBars;
            for (int i = 0; i < numBars; i++) {

                // Usamos directamente 'i' para que el primer elemento quede arriba
                int y_pos = topM + (int) ((i * stepY) + (stepY / 2));

                String label = categories.get(i);
                // Alineación a la derecha contra el margen izquierdo
                g2.drawString(label, leftM - fm.stringWidth(label) - 10, y_pos + (fm.getAscent() / 2) - 2);
            }
        }
    }
}