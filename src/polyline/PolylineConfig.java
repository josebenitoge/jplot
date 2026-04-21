package polyline;

import model.Config;
import model.Pixel;
import java.awt.*;
import java.util.List;

public class PolylineConfig extends Config {

    public PolylineConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (getDataset() != null) {
            new PolylineRenderer((PolylineDataset) getDataset(), pixels, left, right, top, bottom).bind();
        }
    }

    // Sobrescribimos el dibujado de etiquetas para soportar el formateo de Categorías en X
    @Override
    public void drawAxisLabels(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        PolylineDataset ds = (PolylineDataset) getDataset();
        
        // Si hay categorías de texto almacenadas, dibujamos con texto
        if (ds != null && !ds.getXCategories().isEmpty()) {
            
            double minX = ds.getMinX();
            double maxX = ds.getMaxX();
            double minY = ds.getMinY();
            double maxY = ds.getMaxY();

            if (maxX == minX) { maxX += 1; minX -= 1; }
            if (maxY == minY) { maxY += 1; minY -= 1; }

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();

            // Dibujar Eje X (Con Textos)
            List<String> cats = ds.getXCategories();
            int numTicksX = Math.max(1, (rightM - leftM) / 60);

            for (int i = 0; i <= numTicksX; i++) {
                int x_pos = leftM + i * (rightM - leftM) / numTicksX;
                double val = minX + i * (maxX - minX) / numTicksX;
                
                // Redondeamos el valor matemático para encontrar su índice en la lista de textos
                int index = (int) Math.round(val);
                String label;
                if (index >= 0 && index < cats.size()) {
                    label = cats.get(index);
                } else {
                    label = String.format("%.1f", val); // Fallback de seguridad
                }
                
                g2.drawString(label, x_pos - (fm.stringWidth(label) / 2), bottomM + 20);
            }

            // Dibujar Eje Y (Números normales)
            int numTicksY = Math.max(1, (bottomM - topM) / 40);
            for (int i = 0; i <= numTicksY; i++) {
                int y_pos = bottomM - i * (bottomM - topM) / numTicksY;
                double val = minY + i * (maxY - minY) / numTicksY;
                String label = String.format("%.1f", val);
                g2.drawString(label, leftM - fm.stringWidth(label) - 10, y_pos + 5);
            }
            
        } else {
            // Si no usó textos, que dibuje los números estándar de tu Config base
            super.drawAxisLabels(g2, leftM, rightM, topM, bottomM);
        }
    }
}