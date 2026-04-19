package pie;

import model.Config;
import model.Dataset;
import model.Pixel;
import model.Point;
import model.Series;

import java.awt.*;

public class PieConfig extends Config {

    public PieConfig(String title) {
        super(title, "");
    }



    // 1. Quitamos los textos de los ejes
    @Override
    public void drawAxisLabels(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        // Vacío
    }

    // 2. Quitamos el Grid Horizontal
    @Override
    public void drawHorizontalGrid(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        // Vacío
    }

    // 3. Quitamos el Grid Vertical
    @Override
    public void drawVerticalGrid(Graphics2D g2, int leftM, int rightM, int topM, int bottomM) {
        // Vacío
    }

    // 4. Quitamos los Ticks de los bordes (el marco negro)
    @Override
    public void bindBorders(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (pixels.length == 0) return;

        // Si quieres mantener el recuadro negro exterior, deja este bucle.
        // Si quieres que la pizza flote en el blanco total, borra también esto.
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                boolean isVert = (i == left || i == right) && (j >= top && j <= bottom);
                boolean isHorz = (j == top || j == bottom) && (i >= left && i <= right);
                if (isVert || isHorz) pixels[i][j].color = Color.BLACK;
            }
        }

        // NOTA: Al no poner aquí los bucles de "ticks" que tiene el padre,
        // las marcas de los ejes desaparecen por completo de la matriz de píxeles.
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (getDataset() != null) {
            new PieRenderer(getDataset(), pixels, left, right, top, bottom).bind();
        }
    }

    @Override
    public void drawLegend(Graphics2D g2, int x, int y) {
        Dataset dataset = getDataset();
        if (dataset == null || dataset.series().isEmpty()) return;

        // 1. Calcular el total acumulado de todas las porciones
        double total = 0;
        for (Series s : dataset.series().values()) {
            for (model.Point p : s.points()) total += p.getY();
        }
        if (total == 0) return;

        // 2. Configurar la caja de la leyenda
        // La hacemos un poco más ancha (180px) para que quepa bien el "Nombre (00.0%)"
        int boxWidth = 180;
        int boxHeight = 20 + (dataset.series().size() * 20);

        g2.setColor(new Color(248, 248, 248, 220)); // Un pelín de transparencia
        g2.fillRoundRect(x, y, boxWidth, boxHeight, 10, 10);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRoundRect(x, y, boxWidth, boxHeight, 10, 10);

        // 3. Pintar cada entrada de la leyenda
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int i = 0;
        for (Series s : dataset.series().values()) {
            // Calcular valor de esta serie
            double seriesVal = 0;
            for (Point p : s.points()) seriesVal += p.getY();

            double percentage = (seriesVal / total) * 100;

            // Dibujar el indicador de color (un cuadradito suele quedar mejor que una línea en tartas)
            g2.setColor(s.color());
            g2.fillRect(x + 10, y + 15 + (i * 20), 12, 12);
            g2.setColor(new Color(0,0,0,50)); // Un bordecito sutil al cuadrado
            g2.drawRect(x + 10, y + 15 + (i * 20), 12, 12);

            // Dibujar Texto: "Nombre (XX.X%)"
            g2.setColor(Color.BLACK);
            String legendText = String.format("%s (%.1f%%)", s.name(), percentage);
            g2.drawString(legendText, x + 30, y + 26 + (i * 20));

            i++;
        }
    }

}