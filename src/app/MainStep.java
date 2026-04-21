package app;

import ui.Plotter;
import java.awt.Color;

public class MainStep {

    public static void main(String[] args) {
        Plotter p = new Plotter("Epoch: 1970-01-01T00:00:00", Plotter.STEP_PLOT, "", "");

        p.create(new Color(0, 85, 140), "name", "Estado de Señal");

        // Fíjate cómo la gráfica de la imagen empieza en Y=0 y sube a Y=10 en el origen.
        // Para lograr eso con un Step Plot, introducimos un punto inicial y luego el salto.
        p.add("Estado de Señal", "00:00.000000", 0.0);
        p.add("Estado de Señal", "00:00.000000", 11.0); 

        // Los siguientes saltos ocurren en los distintos timestamps
        p.add("Estado de Señal", "00:00.000020", 26.0);
        p.add("Estado de Señal", "00:00.000040", 58.0);
        p.add("Estado de Señal", "00:00.000060", 64.0);
        p.add("Estado de Señal", "00:00.000080", 95.0);
        p.add("Estado de Señal", "00:00.000100", 99.0);

        p.plot();
        p.img(1920, 1080, "./etc/23_step.png");
    }
}