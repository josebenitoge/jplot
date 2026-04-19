package app;

import ui.Plotter;
import java.awt.Color;
import java.util.Random;

public class MainBubbleGalaxySimulation {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Simulación de Densidad Estelar (Formación Espiral)", Plotter.BUBBLE_CHART, "Parsec X", "Parsec Y");
        // Apagamos la cuadrícula para darle un aspecto más limpio y científico
        p.grid(false, false);

        // Un azul eléctrico muy translúcido (Alpha = 40)
        p.create(new Color(0, 0, 0, 255), "name", "Estrellas Jóvenes", "type", "BUBBLE");
        // Un naranja rojizo para el núcleo galáctico
        p.create(new Color(191, 34, 72, 226), "name", "Núcleo Galáctico", "type", "BUBBLE");

        Random rand = new Random();
        int numStars = 4000;
        int arms = 3; // Número de brazos de la galaxia

        // 1. Dibujamos los brazos espirales (Estrellas Jóvenes)
        for (int i = 0; i < numStars; i++) {
            double r = rand.nextDouble() * 100; // Distancia desde el centro
            double theta = r * 0.1 + (2 * Math.PI / arms) * rand.nextInt(arms); // Ángulo de la espiral
            
            // Añadimos algo de ruido (dispersión) para que no sea una línea perfecta
            double dispersion = (100 - r) * 0.05 * rand.nextGaussian(); 
            theta += dispersion;

            double x = r * Math.cos(theta);
            double y = r * Math.sin(theta);
            
            // Las estrellas lejanas son más pequeñas
            double size = 1 + rand.nextDouble() * (200 / (r + 10)); 
            
            p.add("Estrellas Jóvenes", x, y, size);
        }

        // 2. Dibujamos el núcleo superdenso en el centro
        for (int i = 0; i < 800; i++) {
            double x = rand.nextGaussian() * 5; 
            double y = rand.nextGaussian() * 5;
            double size = 5 + rand.nextDouble() * 25;
            p.add("Núcleo Galáctico", x, y, size);
        }

        // Exportación de la simulación
        p.img(1920, 1080, "./ejemplos/08_bubble_galaxy_sim.png");
    }
}