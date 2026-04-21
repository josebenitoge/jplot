package multi;

import ui.Plotter;
import ui.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

/**
 * Motor de composición que permite agrupar múltiples instancias de Plotter 
 * en una sola vista o imagen. Gestiona la disposición en rejilla (Grid Layout).
 */
public class MultiPlotter {

    private final List<Plotter> plots = new ArrayList<>();
    private final int rows;
    private final int cols;
    private final String title;

    /**
     * Crea un nuevo gestor de multi-gráficos.
     * @param title Título del dashboard.
     * @param rows Número de filas.
     * @param cols Número de columnas.
     */
    public MultiPlotter(String title, int rows, int cols) {
        this.title = title;
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Añade un gráfico a la siguiente posición disponible en la rejilla.
     * @param p Instancia de Plotter ya configurada.
     */
    public void add(Plotter p) {
        if (plots.size() < rows * cols) {
            plots.add(p);
        } else {
            System.err.println("MultiPlotter: No hay más espacio en la rejilla " + rows + "x" + cols);
        }
    }

    /**
     * Muestra todos los gráficos en una única ventana de escritorio.
     */
    public void plot() {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(rows, cols, 10, 10)); // 10px de separación

        for (Plotter p : plots) {
            frame.add(p.getPanel());
        }

        frame.pack();
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Exporta el dashboard completo a un único archivo de imagen.
     * @param width Ancho total de la imagen final.
     * @param height Alto total de la imagen final.
     * @param path Ruta del archivo (ej: "dashboard.png").
     */
    public void img(int width, int height, String path) {
        BufferedImage dashboard = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dashboard.createGraphics();
        
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        int cellWidth = width / cols;
        int cellHeight = height / rows;

        for (int i = 0; i < plots.size(); i++) {
            int r = i / cols;
            int c = i % cols;

            // Creamos un panel temporal para el renderizado headless
            ChartPanel panel = plots.get(i).getPanel();
            panel.setBounds(0, 0, cellWidth, cellHeight);
            
            // Forzamos el cálculo de la matriz de píxeles para esta resolución de celda
            // Nota: Se requiere que fillMatrix sea accesible (protected o public)
            // panel.fillMatrix(cellWidth, cellHeight);

            // Trasladamos el contexto gráfico a la celda correspondiente
            Graphics2D gCell = (Graphics2D) g2.create(c * cellWidth, r * cellHeight, cellWidth, cellHeight);
            panel.paint(gCell);
            gCell.dispose();
        }

        g2.dispose();

        try {
            ImageIO.write(dashboard, "png", new File(path));
            System.out.println("Dashboard exportado correctamente a: " + path);
        } catch (Exception e) {
            System.err.println("Error al exportar dashboard: " + e.getMessage());
        }
    }
}