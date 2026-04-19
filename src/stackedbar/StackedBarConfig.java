package stackedbar;

import model.Config;
import model.Pixel;
import model.Point;
import model.Series;
import java.awt.*;
import java.util.List;

public class StackedBarConfig extends Config {

    public StackedBarConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (getDataset() != null) {
            new StackedBarRenderer(getDataset(), pixels, left, right, top, bottom).bind();
        }
    }

    @Override
    public void drawAxisLabels(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        StackedBarDataset sData = (StackedBarDataset) getDataset();
        if (sData == null) return;

        List<String> categories = sData.getCategories();
        double minY = 0.0;
        double maxY = sData.getMaxY();
        if (maxY == 0) maxY = 1.0;

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();

        // 1. Dibujar Eje Y (Números en el lateral)
        g2.setColor(Color.BLACK);
        int numTicksY = Math.max(1, (bottomM - topM) / 40);
        for (int i = 0; i <= numTicksY; i++) {
            int y_pos = bottomM - i * (bottomM - topM) / numTicksY;
            double val = minY + i * (maxY - minY) / numTicksY;
            String label = (val == Math.floor(val)) ? String.format("%.0f", val) : String.format("%.1f", val);
            g2.drawString(label, leftM - fm.stringWidth(label) - 10, y_pos + 5);
        }

        // 2. Dibujar Eje X (Categorías abajo)
        int numBars = categories.size();
        if (numBars > 0) {
            double stepX = (double) (rightM - leftM) / numBars;
            for (int i = 0; i < numBars; i++) {
                int x_pos = leftM + (int) ((i * stepX) + (stepX / 2));
                String label = categories.get(i);
                g2.drawString(label, x_pos - (fm.stringWidth(label) / 2), bottomM + 20);
            }

            // 3. Dibujar textos DENTRO de los bloques de color
            int graphHeight = bottomM - topM;
            double[] categoryAccumulators = new double[numBars]; // Para saber la altura de la pila

            for (Series set : sData.series().values()) {
                for (Point p : set.points()) {
                    int xIndex = (int) p.getX();
                    double val = p.getY();

                    if (val > 0) {
                        double previousSum = categoryAccumulators[xIndex];
                        double currentSum = previousSum + val;

                        // Encontrar el valor medio del bloque para centrar el texto
                        double midValueY = previousSum + (val / 2.0);
                        double ratioY = midValueY / maxY;
                        int pixelY = bottomM - (int) (ratioY * graphHeight);

                        int centerX = leftM + (int) ((xIndex * stepX) + (stepX / 2));

                        // Imprimir el valor sin decimales, como en tu imagen
                        String text = String.format("%.0f", val);
                        g2.drawString(text, centerX - (fm.stringWidth(text) / 2), pixelY + (fm.getAscent() / 2) - 2);

                        // Actualizar la pila
                        categoryAccumulators[xIndex] = currentSum;
                    }
                }
            }
        }
    }
}