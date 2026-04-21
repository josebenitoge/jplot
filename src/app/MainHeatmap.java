package app;

import ui.Plotter;
import java.awt.*;

public class MainHeatmap {
    public static void main(String[] args) {
        Plotter p = new Plotter("Análisis Multi-Clase de Densidad", Plotter.HEATMAP, "X", "Y");

        // Configuramos el fondo morado que definimos en el Config
        // (Asumiendo que añadiste el método setBackground o lo dejaste por defecto)

        // --- SERIE 1: SENSORES (NARANJA) ---
        p.create(new Color(0xFFBE05), "name", "Sensores");

        // Clúster inferior izquierdo
        p.add("Sensores", 10, 10, 85);
        p.add("Sensores", 15, 12, 70);
        p.add("Sensores", 12, 20, 90);
        p.add("Sensores", 25, 15, 60);

        // Puntos dispersos
        p.add("Sensores", 50, 50, 95); // Gran intensidad central
        p.add("Sensores", 45, 55, 80);
        p.add("Sensores", 10, 90, 50);
        p.add("Sensores", 90, 10, 40);
        p.add("Sensores", 30, 40, 65);
        p.add("Sensores", 70, 20, 75);
        p.add("Sensores", 5, 45, 55);
        p.add("Sensores", 40, 80, 45);
        p.add("Sensores", 20, 70, 60);
        p.add("Sensores", 60, 10, 30);
        p.add("Sensores", 80, 5, 85);

        // --- SERIE 2: ACTUADORES (CIAN) ---
        p.create(new Color(0x30DC61), "name", "Actuadores");

        // Clúster superior derecho
        p.add("Actuadores", 85, 85, 90);
        p.add("Actuadores", 80, 90, 75);
        p.add("Actuadores", 90, 80, 80);
        p.add("Actuadores", 75, 75, 65);

        // Puntos de interferencia (cerca de la otra clase)
        p.add("Actuadores", 55, 45, 85); // Cerca del centro naranja
        p.add("Actuadores", 60, 60, 70);
        p.add("Actuadores", 35, 35, 50);
        p.add("Actuadores", 20, 30, 40);
        p.add("Actuadores", 95, 50, 95);
        p.add("Actuadores", 50, 5, 60);
        p.add("Actuadores", 10, 60, 45);
        p.add("Actuadores", 65, 95, 70);
        p.add("Actuadores", 40, 25, 55);
        p.add("Actuadores", 75, 45, 80);
        p.add("Actuadores", 30, 90, 40);

        p.plot();

        p.img(1920, 1080, "./etc/26_heatmap.png");
    }
}