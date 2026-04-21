package app;

import ui.Plotter;
import java.awt.Color;
import java.util.Random;

public class MainHistogram {
    public static void main(String[] args) {
        // Inicializamos la fachada
        Plotter p = new Plotter("Distribución Normal", Plotter.HISTOGRAM_PLOT, "Valor", "Frecuencia");

        // Creamos la serie especificando 45 bins (como en tu imagen)
        p.create(new Color(47, 77, 216), "name", "Datos", "bins", "45");

        // Alimentamos el motor exclusivamente con datos puros (1 dimensión).
        // El sistema calculará los intervalos y las alturas de forma invisible.
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            p.add("Datos", rand.nextGaussian());
        }

        p.plot();
        p.img(1920, 1080, "./etc/25_histogram.png");
    }
}