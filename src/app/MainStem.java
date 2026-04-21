package app;

import ui.Plotter;
import java.awt.Color;

public class MainStem {

    public static void main(String[] args) {
        // 1. Inicialización de la fachada
        Plotter p = new Plotter("Análisis de Señales Discretas (FIR)", Plotter.STEM_PLOT, "Muestras (n)", "Amplitud");

        // Configuramos un fondo oscuro para resaltar el Anti-Aliasing y los colores
        p.setBackground(new Color(25, 25, 30));

        // ==========================================
        // SEÑAL 1: Seno amortiguado (Círculos Macizos)
        // ==========================================
        // Usamos el varargs para configurar nombre y estilo directamente
        p.create(new Color(47, 70, 170), "name", "x[n]", "style", "CIRCLE_SOLID");

        for (int n = 0; n <= 20; n++) {
            double amplitud = Math.sin(n * 0.5) * Math.exp(-n * 0.1) * 10;
            p.add("x[n]", n, amplitud);
        }

        // ==========================================
        // SEÑAL 2: Coseno de alta frecuencia (Cuadrados Huecos)
        // ==========================================
        p.create(new Color(230, 87, 27), "name", "h[n]", "style", "SQUARE_HOLLOW");

        for (int n = 0; n <= 20; n++) {
            double amplitud = Math.cos(n * 1.2) * 4 - 2;
            p.add("h[n]", n + 0.2, amplitud); // Desplazamos un poco en X para evitar colisión exacta
        }

        // ==========================================
        // SEÑAL 3: Impulsos aislados (Rombos/Diamantes)
        // ==========================================
        p.create(new Color(58, 131, 60), "name", "Ruido", "style", "LOZENGE_SOLID");

        p.add("Ruido", 5, 8);
        p.add("Ruido", 12, -7);
        p.add("Ruido", 18, 6);

        // ==========================================
        // RENDERIZADO FINAL
        // ==========================================
        p.plot();

        p.img(1920, 1080, "./etc/24_stem.png");
    }
}