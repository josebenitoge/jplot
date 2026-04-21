package app;

import ui.Plotter;
import java.awt.Color;

public class MainSineWave {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Propagación de Ondas (Seno vs Coseno)", Plotter.LINE_CHART, "Tiempo (t)", "Amplitud (A)");
        p.grid(true, true);
        // Onda Seno (Azul Cyan)
        p.create(new Color(0, 180, 216), "name", "Onda Seno", "type", "FUNCTION", "style", "SOLID");
        
        // Onda Coseno (Rosa Magenta)
        p.create(new Color(220, 20, 140), "name", "Onda Coseno", "type", "FUNCTION", "style", "SOLID");

        // Línea base del eje X (Gris punteado)
        p.create(new Color(150, 150, 150), "name", "Reposo", "type", "FUNCTION", "style", "DASHED");

        // Rango de 0 a 4 Pi (Dos ciclos completos)
        double endX = 4 * Math.PI;

        // Le damos 1500 pasos para una resolución perfecta de la curva
        p.add("Onda Seno", 0.0, endX, 1500, x -> Math.sin(x));
        p.add("Onda Coseno", 0.0, endX, 1500, x -> Math.cos(x));
        p.add("Reposo", 0.0, endX, 100, x -> 0.0);
        p.plot();
        p.img(1920, 1080, "./etc/13_sine_waves.png");
    }
}