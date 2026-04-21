package ui;

import bubble.BubbleDataset;
import line.LineDataset;
import model.Config;
import model.Dataset;

import javax.swing.*;
import java.awt.*;
import java.util.function.DoubleUnaryOperator;

/**
 * Clase principal y fachada (Facade) del motor de renderizado jPlot.
 * Proporciona una API unificada para la creación, configuración y exportación
 * de diferentes tipos de gráficos (Líneas, Barras, Burbujas, Sectores, etc.).
 * Permite tanto la visualización en ventana (Swing) como la exportación "headless" a imagen.
 */
public class Plotter {

    public static final String POLYLINE_PLOT = "POLYLINE";

    public static final String HISTOGRAM_PLOT = "HISTOGRAM";

    public static final String STEP_PLOT = "STEP";

    public static final String CONFUSION_MATRIX = "CONFUSION";
    public static final String HEATMAP = "HEATMAP";

    public static final String STEM_PLOT = "STEM";

    /** Constante para el gráfico de líneas continuas o funciones. */
    public static final String LINE_CHART = "LINE";

    /** Constante para el gráfico de burbujas (3 dimensiones de datos). */
    public static final String BUBBLE_CHART = "BUBBLE";

    /** Constante para el gráfico de barras verticales. */
    public static final String BAR_CHART = "BAR";

    /** Constante para el gráfico de sectores (tarta). */
    public static final String PIE_CHART = "PIE";

    /** Constante para el gráfico de barras horizontales. */
    public static final String HBAR_CHART = "HBAR";

    /** Constante para el gráfico de barras apiladas. */
    public static final String STACKED_BAR_CHART = "STACKED_BAR";

    private final String title;
    private final Config info;
    private final Dataset dataset;
    private boolean gridH = true, gridV = true;

    /**
     * Construye un nuevo Plotter instanciando el motor de renderizado adecuado
     * basándose en el tipo de gráfico solicitado.
     *
     * @param title     El título general del gráfico que aparecerá en la parte superior.
     * @param chartType El tipo de gráfico a renderizar. Debe ser una de las constantes de esta clase (ej. {@link #LINE_CHART}).
     * @param xTitle    El título o etiqueta para el eje X (horizontal).
     * @param yTitle    El título o etiqueta para el eje Y (vertical).
     */
    public Plotter(String title, String chartType, String xTitle, String yTitle) {
        this.title = title;

        // LA FÁBRICA: El Plotter decide qué motor arrancar basándose en el String
        if (BUBBLE_CHART.equalsIgnoreCase(chartType)) {
            Dataset bData = new BubbleDataset();
            bubble.BubbleConfig bConfig = new bubble.BubbleConfig(xTitle, yTitle);
            bConfig.setDataset(bData);

            this.info = bConfig;
            this.dataset = bData;
        } else if (BAR_CHART.equalsIgnoreCase(chartType)) {
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
        } else if (HBAR_CHART.equalsIgnoreCase(chartType)) {
            hbar.HBarDataset hbData = new hbar.HBarDataset();
            hbar.HBarConfig hbConfig = new hbar.HBarConfig(xTitle, yTitle);
            hbConfig.setDataset(hbData);

            this.info = hbConfig;
            this.dataset = hbData;
        }   else if (CONFUSION_MATRIX.equalsIgnoreCase(chartType)) {
            confusion.ConfusionDataset cData = new confusion.ConfusionDataset();
            confusion.ConfusionConfig cConfig = new confusion.ConfusionConfig(xTitle, yTitle);
            cConfig.setDataset(cData);
            this.info = cConfig;
            this.dataset = cData;
        }else if (HEATMAP.equalsIgnoreCase(chartType)) {
            heatmap.HeatmapDataset hData = new heatmap.HeatmapDataset();
            heatmap.HeatmapConfig hConfig = new heatmap.HeatmapConfig(xTitle, yTitle);
            hConfig.setDataset(hData);
            this.info = hConfig;
            this.dataset = hData;
            this.grid(false);
            this.setBackground(new Color(0x1C1647));
        } else if (STEM_PLOT.equalsIgnoreCase(chartType)) {
            stem.StemDataset sData = new stem.StemDataset();
            stem.StemConfig sConfig = new stem.StemConfig(xTitle, yTitle);
            sConfig.setDataset(sData);
            this.info = sConfig;
            this.dataset = sData;
        }else if (POLYLINE_PLOT.equalsIgnoreCase(chartType)) {
            polyline.PolylineDataset pData = new polyline.PolylineDataset();
            polyline.PolylineConfig pConfig = new polyline.PolylineConfig(xTitle, yTitle);
            pConfig.setDataset(pData);
            this.info = pConfig;
            this.dataset = pData;
        }else if (STEP_PLOT.equalsIgnoreCase(chartType)) {
            step.StepDataset sData = new step.StepDataset();
            step.StepConfig sConfig = new step.StepConfig(xTitle, yTitle);
            sConfig.setDataset(sData);
            this.info = sConfig;
            this.dataset = sData;
        }else if (HISTOGRAM_PLOT.equalsIgnoreCase(chartType)) {
            histogram.HistogramDataset hData = new histogram.HistogramDataset();
            histogram.HistogramConfig hConfig = new histogram.HistogramConfig(xTitle, yTitle);
            hConfig.setDataset(hData);
            this.info = hConfig;
            this.dataset = hData;
        }else {
            // Por defecto, construimos el universo de líneas continuas
            LineDataset lData = new LineDataset();
            line.ChartConfig lConfig = new line.ChartConfig(xTitle, yTitle);
            lConfig.setDataset(lData);

            this.info = lConfig;
            this.dataset = lData;
        }
    }

    /**
     * Renderiza el gráfico en una ventana nativa del sistema operativo usando Swing.
     * Útil para visualización en tiempo real durante el desarrollo o en aplicaciones de escritorio.
     */
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

    /**
     * Define y crea una nueva serie de datos dentro del gráfico.
     *
     * @param color El color principal que representará a esta serie.
     * @param info  Pares de clave-valor con la configuración de la serie
     * (ej. "name", "MiSerie", "type", "LINE", "style", "SOLID").
     */
    public void create(Color color, String... info) {
        dataset.create(color, info);
    }

    /**
     * Añade un nuevo punto de datos a una serie existente usando coordenadas puramente numéricas.
     *
     * @param series El nombre de la serie a la que se añadirá el dato.
     * @param x      Las coordenadas numéricas del punto. Para 2D (x, y). Para burbujas (x, y, tamaño).
     */
    public void add(String series, double... x) {
        dataset.add(series, x);
    }

    /**
     * Añade un nuevo punto de datos a una serie existente usando una categoría (texto) en lugar de una coordenada X.
     * Orientado principalmente a gráficos de barras o sectores.
     *
     * @param name     El nombre de la serie.
     * @param category La etiqueta categórica (ej. "Q1", "Europa", "Java").
     * @param value    El valor numérico asociado a la categoría.
     */
    public void add(String name, String category, double value) {
        dataset.add(name, category, value);
    }

    /**
     * Evalúa una función matemática nativa de Java y genera los puntos automáticamente
     * para dibujarla en el lienzo.
     *
     * @param seriesName El nombre de la serie bajo la que se registrará la función.
     * @param minX       El límite inferior del dominio a evaluar (Eje X).
     * @param maxX       El límite superior del dominio a evaluar (Eje X).
     * @param numPoints  La resolución de la curva. A mayor número, más suave será el trazo.
     * @param function   Expresión Lambda que define la función (ej. {@code x -> Math.sin(x)}).
     */
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
            double y = function.applyAsDouble(x);

            // Reutilizamos el motor interno pasando X e Y
            dataset.add(seriesName, x, y);
        }
    }

    /**
     * Activa o desactiva la cuadrícula general del gráfico de forma global (ambos ejes).
     *
     * @param grid {@code true} para mostrar la cuadrícula, {@code false} para ocultarla.
     */
    public void grid(boolean grid) {
        this.gridH = grid;
        this.gridV = grid;
    }

    /**
     * Controla la visibilidad de la cuadrícula de forma independiente para cada eje.
     *
     * @param h {@code true} para dibujar las líneas horizontales.
     * @param v {@code true} para dibujar las líneas verticales.
     */
    public void grid(boolean h, boolean v) {
        this.gridH = h;
        this.gridV = v;
    }

    /**
     * Activa o desactiva las líneas de seguimiento (crosshair) que acompañan al cursor
     * al interactuar con el gráfico en el modo UI.
     *
     * @param showX {@code true} para mostrar la línea vertical del cursor.
     * @param showY {@code true} para mostrar la línea horizontal del cursor.
     */
    public void crosshair(boolean showX, boolean showY) {
        if (info != null) {
            info.setCrosshair(showX, showY);
        }
    }

    /**
     * Exporta el gráfico actual a un archivo de imagen utilizando la resolución por defecto (800x600).
     * El renderizado es "headless", no requiere abrir ninguna ventana.
     *
     * @param path La ruta del archivo destino (ej. "output.png").
     */
    public void img(String path) {
        img(800, 600, path);
    }

    /**
     * Exporta el gráfico actual a un archivo de imagen definiendo una resolución personalizada.
     * Implementa un sistema de renderizado "headless" puro mediante pre-cálculo de matrices.
     *
     * @param width  La anchura de la imagen en píxeles.
     * @param height La altura de la imagen en píxeles.
     * @param path   La ruta del archivo destino. Soporta auto-detección de extensión (.png, .jpg).
     */
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

    /**
     * Crea y devuelve un panel de gráfico listo para ser integrado en otros contenedores.
     * @return Una instancia de ChartPanel configurada con los datos actuales.
     */
    public ChartPanel getPanel() {
        info.setGridH(gridH);
        info.setGridV(gridV);
        return new ChartPanel(new LoadingPanel(), info);
    }

    // Dentro de ui.Plotter.java
    /**
     * Cambia el color de fondo del área de renderizado del gráfico.
     */
    public void setBackground(Color color) {
        if (this.info != null) {
            this.info.setBackgroundColor(color);
        }
    }
}