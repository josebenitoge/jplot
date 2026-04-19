package bar;

import model.Config;
import model.Pixel;
import java.awt.*;
import java.util.List;

public class BarConfig extends Config {

    public BarConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (getDataset() != null) {
            new BarRenderer(getDataset(), pixels, left, right, top, bottom).bind();
        }
    }

    // ¡SOBREESCRIBIMOS EL COMPORTAMIENTO DEL EJE X!
    @Override
    public void drawAxisLabels(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        BarDataset bData = (BarDataset) getDataset();
        if (bData == null) return;

        List<String> categories = bData.getCategories();
        double minY = 0.0; // En barras, el Y casi siempre empieza en 0
        double maxY = bData.getMaxY();
        if (maxY == 0) maxY = 1.0;

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();

        // 1. Dibujar Eje Y (Igual que en lineas)
        int numTicksY = Math.max(1, (bottomM - topM) / 40);
        for (int i = 0; i <= numTicksY; i++) {
            int y_pos = bottomM - i * (bottomM - topM) / numTicksY;
            double val = minY + i * (maxY - minY) / numTicksY;
            String label = String.format("%.2f", val);
            g2.drawString(label, leftM - fm.stringWidth(label) - 10, y_pos + 5);
        }

        // 2. Dibujar Eje X (¡Basado en las categorías!)
        int numBars = categories.size();
        if (numBars > 0) {
            double stepX = (double) (rightM - leftM) / numBars;
            for (int i = 0; i < numBars; i++) {
                // Posicionamos el texto justo en el centro de donde irá la barra
                int x_pos = leftM + (int) ((i * stepX) + (stepX / 2));
                String label = categories.get(i);
                g2.drawString(label, x_pos - (fm.stringWidth(label) / 2), bottomM + 20);
            }
        }
    }
}