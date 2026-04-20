package ui;

import model.*;
import model.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Lienzo principal del motor de renderizado jPlot.
 * Esta clase actúa como puente entre el framework gráfico de Swing y el motor
 * de renderizado por software interno basado en matrices de píxeles.
 * * Gestiona el pipeline de dibujado en capas:
 * 1. Cuadrícula (Grid) inferior.
 * 2. Matriz de píxeles (Gráfica pura con antialiasing).
 * 3. Elementos vectoriales superpuestos (Ejes, textos, leyenda, tooltips).
 */
public class ChartPanel extends JPanel {

    private Pixel[][] pixels;
    private boolean isLoading = false;
    private final LoadingPanel loading;
    private final Config info;

    /** Margen izquierdo del área útil de la gráfica respecto al ancho total. */
    public int leftM;
    /** Margen derecho del área útil de la gráfica respecto al ancho total. */
    public int rightM;
    /** Margen superior del área útil de la gráfica respecto a la altura total. */
    public int topM;
    /** Margen inferior del área útil de la gráfica respecto a la altura total. */
    public int bottomM;

    /**
     * Construye el panel de renderizado principal.
     *
     * @param loading Componente encargado de mostrar la animación de carga.
     * @param info    Configuración y origen de los datos a renderizar.
     */
    public ChartPanel(LoadingPanel loading, Config info) {
        setBackground(Color.WHITE);
        this.loading = loading;
        this.info = info;
        setToolTipText(""); // Habilita el sistema de Tooltips nativo de Swing
    }

    /**
     * Cambia el estado del renderizador entre "cargando" y "listo".
     * Si está cargando, el pipeline gráfico se suspende para mostrar la animación.
     *
     * @param loading true para suspender el renderizado y mostrar la carga.
     */
    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    /**
     * Sobrescribe la instanciación del tooltip por defecto de Swing para
     * inyectar un componente con diseño personalizado (bordes redondeados y fondo limpio).
     *
     * @return Un objeto JToolTip personalizado.
     */
    @Override
    public JToolTip createToolTip() {
        JToolTip tip = new JToolTip() {
            {
                // Hacemos el fondo original transparente y añadimos margen interno
                setOpaque(false);
                setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. Dibujar el fondo blanco con bordes redondos (radio 12)
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                // 2. Dibujar un borde sutil gris claro
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
                // Dejamos que Swing dibuje el texto HTML por encima
                super.paintComponent(g);
            }
        };
        tip.setComponent(this);
        return tip;
    }

    /**
     * Calcula dinámicamente el contenido del tooltip basándose en la posición del ratón.
     * Implementa un algoritmo de búsqueda de vecino más cercano (Nearest Neighbor)
     * iterando sobre los datos matemáticos y proyectándolos a coordenadas de pantalla.
     *
     * @param e El evento del ratón que disparó el tooltip.
     * @return Código HTML formateado con la información del punto más cercano,
     * o null si el cursor está fuera del área útil.
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        if (info == null || info.getDataset() == null) return null;

        int mx = e.getX();
        int my = e.getY();

        // Evitar cálculos si el ratón está fuera de los márgenes de la gráfica
        if (mx < leftM || mx > rightM || my < topM || my > bottomM) return null;

        Dataset lineDataset = info.getDataset();
        double minX = lineDataset.getMinX();
        double maxX = lineDataset.getMaxX();
        double minY = lineDataset.getMinY();
        double maxY = lineDataset.getMaxY();

        int targetWidth = rightM - leftM;
        int graphHeight = bottomM - topM;

        if (targetWidth <= 0 || graphHeight <= 0) return null;
        if (maxX == minX) { maxX += 1; minX -= 1; }
        if (maxY == minY) { maxY += 1; minY -= 1; }

        double closestDist = 10.0; // Umbral de tolerancia de 10 píxeles
        String tooltip = null;

        for (Series set : lineDataset.series().values()) {
            for (Point mathDataPoint : set.points()) {

                double ratioX = (mathDataPoint.getX() - minX) / (maxX - minX);
                double ratioY = (mathDataPoint.getY() - minY) / (maxY - minY);

                int pixelX = leftM + (int) (ratioX * targetWidth);
                int pixelY = bottomM - (int) (ratioY * graphHeight);

                double dist = Math.sqrt(Math.pow(mx - pixelX, 2) + Math.pow(my - pixelY, 2));

                if (dist < closestDist) {
                    closestDist = dist;

                    // Convertimos el color de la gráfica a formato Hexadecimal (#RRGGBB)
                    String hexColor = String.format("#%02x%02x%02x",
                            set.color().getRed(), set.color().getGreen(), set.color().getBlue());

                    // Usamos HTML para formatear el texto
                    // &#9679; es un círculo relleno para mostrar el color original de la serie
                    tooltip = String.format("<html><div style='font-family: sans-serif;'>" +
                                            "<b><font color='%s'>&#9679;</font> <font color='black'>%s</font></b><br>" +
                                            "<font color='#777777'>X: %.2f</font><br>" +
                                            "<font color='#777777'>Y: %.2f</font>" +
                                            "</div></html>",
                            hexColor, set.name(), mathDataPoint.getX(), mathDataPoint.getY());
                }
            }
        }
        return tooltip;
    }

    /**
     * Bucle principal de renderizado gráfico.
     * Orquesta el dibujado secuencial del fondo, la matriz de píxeles precalculada
     * y los metadatos vectoriales.
     *
     * @param g Contexto gráfico suministrado por el EDT de Swing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Si estamos cargando datos, delegamos el dibujado al panel de carga
        if (isLoading) {
            loading.setSize(getSize());
            loading.paintComponent(g2);
            return;
        }

        int currentWidth = getWidth();
        int currentHeight = getHeight();

        // Control de reescalado: si la ventana cambió de tamaño, se recalcula la matriz
        if (pixels == null || currentWidth != pixels.length || currentHeight != pixels[0].length) {
            fillMatrix(currentWidth, currentHeight);
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ========================================================
        // 1. DIBUJAR EL GRID (¡Al fondo de todo!)
        // ========================================================
        info.drawHorizontalGrid(g2, leftM, rightM, topM, bottomM);
        info.drawVerticalGrid(g2, leftM, rightM, topM, bottomM);
        info.drawCrosshair(g2, leftM, rightM, topM, bottomM);

        // ========================================================
        // 2. DIBUJAR PÍXELES DE LAS GRÁFICAS (Encima del grid)
        // ========================================================
        if (pixels != null) {
            for (Pixel[] row : pixels) {
                for (Pixel p : row) {
                    if (p.color != Color.WHITE) {
                        g2.setColor(p.color);
                        g2.fillRect(p.x, p.y, 1, 1);
                    }
                }
            }
        }

        // ========================================================
        // 3. DIBUJAR UI EXTERNA (Textos, Ejes, Leyenda)
        // ========================================================
        info.drawAxisLabels(g2, leftM, rightM, topM, bottomM);
        info.drawAxisTitles(g2, leftM, rightM, topM, bottomM);
        info.drawLegend(g2, rightM + 20, topM);
    }

    /**
     * Inicializa y precalcula la matriz bidimensional de píxeles que conforma la gráfica principal.
     * Este método realiza el trabajo matemático pesado antes de la llamada a repaint().
     *
     * @param w Anchura total disponible en el lienzo en el momento actual.
     * @param h Altura total disponible en el lienzo en el momento actual.
     */
    protected void fillMatrix(int w, int h) {
        // Cálculo de márgenes dinámicos en base al tamaño de la ventana
        leftM = (int) (w * 0.08 + 70);
        rightM = w - (int) (w * 0.08 + 120);
        topM = (int) (h * 0.10 + 20);
        bottomM = h - (int) (h * 0.10 + 80);

        if (rightM <= leftM || bottomM <= topM) return;

        pixels = new Pixel[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                pixels[i][j] = new Pixel(i, j, Color.WHITE);
            }
        }

        // Inyección de la matriz en los motores para la computación y mezcla (blending) de los píxeles
        info.bindBorders(pixels, leftM, rightM, topM, bottomM);
        info.bind(pixels, leftM, rightM, topM, bottomM);
    }
}