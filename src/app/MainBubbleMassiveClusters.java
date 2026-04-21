package app;

import ui.Plotter;
import java.awt.Color;
import java.util.Random;

public class MainBubbleMassiveClusters {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Análisis de Agrupaciones (K-Means Clustering)", Plotter.BUBBLE_CHART, "Componente Principal 1", "Componente Principal 2");
        p.grid(true, true);

        // Creamos las series con una opacidad muy baja (Alpha = 60) para destacar el solapamiento
        p.create(new Color(211, 44, 44, 255), "name", "Clúster Alfa", "type", "BUBBLE");
        p.create(new Color(32, 101, 248, 255), "name", "Clúster Beta", "type", "BUBBLE");
        p.create(new Color(59, 237, 59, 136), "name", "Clúster Gamma", "type", "BUBBLE");

        Random rand = new Random();

        // Generamos 1000 puntos para el Clúster Alfa (Centroide en x:-4, y:5)
        for (int i = 0; i < 100; i++) {
            double x = -4 + (rand.nextGaussian() * 2.5); // Desviación estándar de 2.5
            double y = 5 + (rand.nextGaussian() * 2.5);
            double size = 2 + rand.nextDouble() * 15; // Tamaño aleatorio entre 2 y 17
            p.add("Clúster Alfa", x, y, size);
        }

        // Generamos 1000 puntos para el Clúster Beta (Centroide en x:5, y:-3)
        for (int i = 0; i < 100; i++) {
            double x = 5 + (rand.nextGaussian() * 3.0);
            double y = -3 + (rand.nextGaussian() * 3.0);
            double size = 2 + rand.nextDouble() * 20;
            p.add("Clúster Beta", x, y, size);
        }

        // Generamos 1000 puntos para el Clúster Gamma (Centroide en x:2, y:6)
        for (int i = 0; i < 1000; i++) {
            double x = 2 + (rand.nextGaussian() * 2.0);
            double y = 6 + (rand.nextGaussian() * 2.0);
            double size = 2 + rand.nextDouble() * 10;
            p.add("Clúster Gamma", x, y, size);
        }

        p.plot();
        // Renderizado en 4K para máxima nitidez de los millones de píxeles superpuestos
        p.img(1920, 1080, "./etc/07_bubble_clusters_massive.png");
    }
}