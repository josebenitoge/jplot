package confusion;

import model.Config;
import model.Pixel;
import java.awt.*;
import java.util.List;

public class ConfusionConfig extends Config {

    public ConfusionConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        ConfusionDataset ds = (ConfusionDataset) getDataset();
        if (ds == null || ds.getClasses().isEmpty()) return;

        List<String> classes = ds.getClasses();
        int n = classes.size();
        int cellW = (right - left) / n;
        int cellH = (bottom - top) / n;
        double maxVal = ds.getMaxValue();
        Color baseColor = ds.series().get("Matrix").color();

        // Rellenamos la matriz de píxeles (Heatmap)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double val = ds.getValue(classes.get(i), classes.get(j));
                float alpha = (float) (val / maxVal);
                alpha = Math.max(0.05f, Math.min(1.0f, alpha));
                Color cellColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), (int)(alpha * 255));

                int startX = left + j * cellW;
                int startY = top + i * cellH;

                for (int x = startX; x < startX + cellW; x++) {
                    for (int y = startY; y < startY + cellH; y++) {
                        if (x < pixels.length && y < pixels[0].length) {
                            pixels[x][y].color = cellColor;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void drawAxisLabels(Graphics2D g2, int left, int right, int top, int bottom) {
        ConfusionDataset ds = (ConfusionDataset) getDataset();
        if (ds == null) return;

        List<String> classes = ds.getClasses();
        int n = classes.size();
        int cellW = (right - left) / n;
        int cellH = (bottom - top) / n;
        double maxVal = ds.getMaxValue();

        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double val = ds.getValue(classes.get(i), classes.get(j));
                // Decidimos color de texto por contraste
                g2.setColor((val / maxVal) > 0.6 ? Color.WHITE : Color.BLACK);

                String sVal = String.format("%.0f", val);
                int tx = left + j * cellW + (cellW/2) - (fm.stringWidth(sVal)/2);
                int ty = top + i * cellH + (cellH/2) + 5;
                g2.drawString(sVal, tx, ty);
            }
            // Etiquetas de ejes
            g2.setColor(Color.BLACK);
            g2.drawString(classes.get(i), left - fm.stringWidth(classes.get(i)) - 10, top + i * cellH + (cellH/2) + 5);
            g2.drawString(classes.get(i), left + i * cellW + (cellW/2) - (fm.stringWidth(classes.get(i))/2), bottom + 20);
        }
    }
}