package app;

import ui.Plotter;
import confusion.ConfusionDataset;
import java.awt.Color;

public class MainConfusion {
    public static void main(String[] args) {
        // Uso directo de la fachada
        Plotter p = new Plotter("Rendimiento AI", Plotter.CONFUSION_MATRIX, "Predicho", "Real");
        p.grid(false);
        p.create(new Color(52, 152, 219)); // Azul Flat

        // Añadimos datos usando el método add(String, String, double) estándar
        p.add("Gato", "Gato", 50);
        p.add("Gato", "Perro", 2);
        p.add("Perro", "Perro", 40);
        p.add("Perro", "Gato", 8);
        p.plot();
        p.img(500, 500, "etc/22_confusion_matrix.png");
    }
}