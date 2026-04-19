package ui;

import bubble.BubbleDataset;
import line.LineDataset;
import model.Config;
import model.Dataset;

import javax.swing.*;
import java.awt.*;
import java.util.function.DoubleUnaryOperator;

public class Plotter {

    // Constantes públicas para que el usuario sepa qué Strings puede usar
    public static final String LINE_CHART = "LINE";
    public static final String BUBBLE_CHART = "BUBBLE";
    public static final String BAR_CHART = "BAR";
    public static final String PIE_CHART = "PIE";
    public static final String HBAR_CHART = "HBAR";
    public static final String STACKED_BAR_CHART = "STACKED_BAR";

    private final String title;
    private final Config info;
    private final Dataset dataset;
    private boolean gridH = true, gridV = true;

    // EL NUEVO CONSTRUCTOR MAGNÍFICO (Solo pide Strings)
    public Plotter(String title, String chartType, String xTitle, String yTitle) {
        this.title = title;

        // LA FÁBRICA: El Plotter decide qué motor arrancar basándose en el String
        if (BUBBLE_CHART.equalsIgnoreCase(chartType)) {
            // Construimos el universo de burbujas en secreto
            Dataset bData = new BubbleDataset();
            bubble.BubbleConfig bConfig = new bubble.BubbleConfig(xTitle, yTitle);
            bConfig.setDataset(bData);

            this.info = bConfig;
            this.dataset = bData;
        } else if (BAR_CHART.equalsIgnoreCase(chartType)) {
            // ¡NUEVO! Motor de Barras
            bar.BarDataset barData = new bar.BarDataset();
            bar.BarConfig barConfig = new bar.BarConfig(xTitle, yTitle);
            barConfig.setDataset(barData);

            this.info = barConfig;
            this.dataset = barData;
        } else if (PIE_CHART.equalsIgnoreCase(chartType)) {
            pie.PieDataset pData = new pie.PieDataset();
            pie.PieConfig pConfig = new pie.PieConfig(title);
            pConfig.setDataset(pData);
            this.info = pConfig;
            this.dataset = pData;
        } else if (STACKED_BAR_CHART.equalsIgnoreCase(chartType)) {
            stackedbar.StackedBarDataset sData = new stackedbar.StackedBarDataset();
            stackedbar.StackedBarConfig sConfig = new stackedbar.StackedBarConfig(xTitle, yTitle);
            sConfig.setDataset(sData);

            this.info = sConfig;
            this.dataset = sData;
        }else if (HBAR_CHART.equalsIgnoreCase(chartType)) {
            hbar.HBarDataset hbData = new hbar.HBarDataset();
            hbar.HBarConfig hbConfig = new hbar.HBarConfig(xTitle, yTitle);
            hbConfig.setDataset(hbData);

            this.info = hbConfig;
            this.dataset = hbData;
        } else {
            // Por defecto, construimos el universo de líneas continuas
            LineDataset lData = new LineDataset();
            line.ChartConfig lConfig = new line.ChartConfig(xTitle, yTitle);
            lConfig.setDataset(lData);

            this.info = lConfig;
            this.dataset = lData;
        }
    }

    public void plot() {
        info.setGridH(gridH);
        info.setGridV(gridV);
        ChartFrame f = new ChartFrame(info);
        f.setTitle(title);
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setMinimumSize(new Dimension(600, 500));
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public void create(Color color, String... info) {
        dataset.create(color, info);
    }

    public void add(String series, double... x) {
        dataset.add(series, x);
    }

    public void add(String name, String category, double value) {
        dataset.add(name, category, value);
    }

    public void add(String seriesName, double minX, double maxX, int numPoints, DoubleUnaryOperator function) {
        if (numPoints <= 0 || maxX <= minX) {
            System.err.println("Error: Rango o número de puntos inválido para la función.");
            return;
        }

        // Calculamos la distancia entre cada punto a evaluar
        double step = (maxX - minX) / numPoints;

        // Evaluamos la función de izquierda a derecha
        for (int i = 0; i <= numPoints; i++) {
            double x = minX + (i * step);
            double y = function.applyAsDouble(x); // Aquí se ejecuta la matemática del usuario

            // Reutilizamos el motor interno pasando X e Y
            dataset.add(seriesName, x, y);
        }
    }

    // Control del Grid
    public void grid(boolean grid) {
        this.gridH = grid;
        this.gridV = grid;
    }

    public void grid(boolean h, boolean v) {
        this.gridH = h;
        this.gridV = v;
    }

    public void crosshair(boolean showX, boolean showY) {
        if (info != null) {
            info.setCrosshair(showX, showY);
        }
    }

    public void img(String path) {
        img(800, 600, path);
    }

    public void img(int width, int height, String path) {
        // 1. Sincronizamos la configuración visual
        info.setGridH(gridH);
        info.setGridV(gridV);
// 3. Creamos un panel "Fantasma" totalmente independiente de la UI
        ChartPanel headlessPanel = new ChartPanel(new LoadingPanel(), info);

        // ¡CRÍTICO! Le damos un tamaño físico en memoria para que getWidth() funcione
        headlessPanel.setBounds(0, 0, width, height);

        // Pre-calculamos toda la matriz matemática para esta resolución exacta
        headlessPanel.fillMatrix(width, height);

        // 4. Creamos el buffer de la imagen donde vamos a pintar
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Limpiamos el fondo de la imagen con blanco puro
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        // 5. Ordenamos a nuestro panel fantasma que se dibuje sobre la imagen
        headlessPanel.paint(g2);
        g2.dispose();

        // 6. Volcado del archivo al disco
        try {
            java.io.File file = new java.io.File(path);
            // Detectamos si el usuario puso .png o .jpg en la ruta
            String ext = path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : "png";
            javax.imageio.ImageIO.write(image, ext, file);
            System.out.println("Imagen de alta calidad exportada a: " + file.getAbsolutePath());
        } catch (java.io.IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
        }
    }
}