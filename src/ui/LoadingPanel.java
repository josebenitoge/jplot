package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de interfaz de usuario que muestra una animación de carga minimalista.
 * Dibuja una secuencia de hasta tres puntos suspensivos en el centro del componente.
 * Diseñado con un fondo transparente para superponerse sin obstrucciones sobre los lienzos de renderizado.
 */
public class LoadingPanel extends JPanel {

    private int dotCount = 0;

    // El modificador volatile asegura que los cambios en el estado de ejecución
    // sean visibles inmediatamente para el hilo de animación.
    private volatile boolean isRunning = true;

    /**
     * Construye un nuevo LoadingPanel e inicia automáticamente el hilo en segundo plano
     * responsable de actualizar los fotogramas de la animación.
     */
    public LoadingPanel() {
        // Hace que el panel sea transparente para no ocultar el gráfico subyacente
        setOpaque(false);

        // Hilo de animación para ciclar el número de puntos
        Runnable r = () -> {
            try {
                while (isRunning) {
                    // Cicla la cantidad de puntos: 0 -> 1 -> 2 -> 3 -> 0...
                    dotCount = (dotCount + 1) % 4;

                    // Solicita a Swing que repinte el componente en el Event Dispatch Thread (EDT)
                    repaint();

                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        // Nombramos el hilo para facilitar la depuración en caso de volcados de memoria
        new Thread(r, "jPlot-LoadingAnimator").start();
    }

    /**
     * Sobrescribe el método de pintado de Swing para renderizar la secuencia de carga.
     *
     * @param g El contexto gráfico proporcionado por el sistema de ventanas.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Activamos Graphics2D para aplicar suavizado (antialiasing) a los bordes de los círculos
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Configuración geométrica de los puntos
        int dotSize = 10;
        int spacing = 15;
        g2d.setColor(Color.WHITE);

        // Cálculo del eje X inicial para mantener el bloque completo centrado
        // Anchura total = (3 puntos * tamaño) + (2 espacios intermedios * espaciado)
        int totalWidth = (3 * dotSize) + (2 * spacing);
        int startX = (width - totalWidth) / 2;
        int startY = (height - dotSize) / 2;

        // Dibuja la cantidad de puntos correspondientes al estado actual
        for (int i = 0; i < dotCount; i++) {
            int x = startX + (i * (dotSize + spacing));
            g2d.fillOval(x, startY, dotSize, dotSize);
        }
    }

    /**
     * Detiene de forma segura el bucle de animación.
     * Es imperativo invocar este método cuando el proceso de carga termine o el panel
     * sea destruido para evitar fugas de memoria asociadas a hilos huérfanos.
     */
    public void stop() {
        this.isRunning = false;
    }
}