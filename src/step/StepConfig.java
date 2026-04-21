package step;

import model.Config;
import model.Pixel;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

public class StepConfig extends Config {

    public StepConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
        this.backgroundColor = new Color(220, 222, 224); // Gris claro de la imagen
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (getDataset() != null) {
            new StepRenderer((StepDataset) getDataset(), pixels, left, right, top, bottom).bind();
        }
    }

    @Override
    public void drawAxisLabels(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        StepDataset ds = (StepDataset) getDataset();
        
        if (ds != null && !ds.getXCategories().isEmpty()) {
            double minX = ds.getMinX();
            double maxX = ds.getMaxX();
            double minY = ds.getMinY();
            double maxY = ds.getMaxY();

            if (maxX == minX) { maxX += 1; minX -= 1; }
            if (maxY == minY) { maxY += 1; minY -= 1; }

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            FontMetrics fm = g2.getFontMetrics();

            // Dibujar Eje X (Etiquetas ROTADAS)
            List<String> cats = ds.getXCategories();
            int numTicksX = Math.max(1, (rightM - leftM) / 80); // Más espaciado para texto largo

            for (int i = 0; i <= numTicksX; i++) {
                int x_pos = leftM + i * (rightM - leftM) / numTicksX;
                double val = minX + i * (maxX - minX) / numTicksX;
                
                int index = (int) Math.round(val);
                String label = (index >= 0 && index < cats.size()) ? cats.get(index) : "";

                // Rotación de -45 grados usando AffineTransform
                Graphics2D g2Rotated = (Graphics2D) g2.create();
                g2Rotated.translate(x_pos, bottomM + 15);
                g2Rotated.rotate(Math.toRadians(-45));
                g2Rotated.drawString(label, 0, 0);
                g2Rotated.dispose(); // Liberamos el contexto rotado
            }

            // Dibujar Eje Y (Números normales)
            int numTicksY = Math.max(1, (bottomM - topM) / 40);
            for (int i = 0; i <= numTicksY; i++) {
                int y_pos = bottomM - i * (bottomM - topM) / numTicksY;
                double val = minY + i * (maxY - minY) / numTicksY;
                String label = String.format("%.0f", val); // Sin decimales como en la imagen
                g2.drawString(label, leftM - fm.stringWidth(label) - 10, y_pos + 5);
            }
        } else {
            super.drawAxisLabels(g2, leftM, rightM, topM, bottomM);
        }
    }
}