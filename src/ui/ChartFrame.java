package ui;

import model.Config;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Ventana principal (JFrame) que actúa como contenedor visual para los gráficos de jPlot.
 * Esta clase envuelve el lienzo de renderizado (ChartPanel) y gestiona los eventos
 * nativos del sistema operativo, específicamente el redimensionado de la ventana.
 */
public class ChartFrame extends JFrame {

    /** * Temporizador utilizado para aplicar el patrón "Debounce" (anti-rebote)
     * durante la acción de redimensionado de la ventana por parte del usuario.
     */
    private Timer resizeTimer;

    /** Panel superpuesto que muestra la animación interactiva de carga. */
    private LoadingPanel loading;

    /** Lienzo principal donde se ejecuta el pipeline de dibujado de la gráfica. */
    private ChartPanel plotter;

    /**
     * Construye una nueva ventana gráfica y configura los detectores de eventos.
     *
     * @param info Objeto de configuración que contiene los datos y preferencias visuales del gráfico.
     */
    public ChartFrame(Config info) {
        loading = new LoadingPanel();
        plotter = new ChartPanel(loading, info);

        // Establecemos el ChartPanel como el contenido base de la ventana
        setContentPane(plotter);

        // Añadimos un listener para detectar cuándo el usuario cambia el tamaño de la ventana
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handleResizeEvent();
            }
        });

        // Forzamos un primer cálculo de la matriz matemática antes de mostrar la ventana
        plotter.fillMatrix(getWidth(), getHeight());
    }

    /**
     * Maneja de forma optimizada el evento de redimensionado de la ventana.
     * Utiliza un enfoque de "Debouncing" para evitar que el motor de renderizado
     * recalcule la costosa matriz de píxeles decenas de veces por segundo mientras
     * el usuario arrastra el borde de la ventana.
     * * El flujo es:
     * 1. Pausar el renderizado pesado y mostrar la pantalla de carga instantáneamente.
     * 2. Reiniciar un temporizador de 300ms.
     * 3. Si el usuario sigue arrastrando la ventana, el temporizador se reinicia.
     * 4. Cuando el usuario suelta el ratón (pasan 300ms sin eventos), se ejecuta el
     * repintado definitivo a la nueva resolución.
     */
    private void handleResizeEvent() {
        // Si ya hay un temporizador en cuenta regresiva, lo cancelamos
        if (resizeTimer != null && resizeTimer.isRunning()) {
            resizeTimer.stop();
        }

        // Activamos el estado de carga y forzamos a Swing a dibujar el LoadingPanel
        plotter.setLoading(true);
        plotter.repaint();

        // Creamos un nuevo temporizador que se ejecutará 300ms después del ÚLTIMO evento de redimensionado
        resizeTimer = new Timer(300, e -> {
            plotter.setLoading(false);
            // Al hacer repaint con loading = false, ChartPanel detectará el nuevo
            // tamaño y llamará a fillMatrix() de forma segura
            plotter.repaint();
        });

        // Aseguramos que el temporizador se ejecute una sola vez por cada parada
        resizeTimer.setRepeats(false);
        resizeTimer.start();
    }
}