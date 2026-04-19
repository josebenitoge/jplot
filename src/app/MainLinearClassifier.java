package app;

import ui.Plotter;
import java.awt.Color;
import java.util.Random;

public class MainLinearClassifier {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Clasificador de Máquina de Soporte Vectorial (SVM)", Plotter.LINE_CHART, "Característica X", "Característica Y");
        p.grid(true, true);

        // La frontera de decisión (La línea que separa los puntos)
        p.create(new Color(50, 50, 50), "name", "Hiperplano de Separación", "type", "FUNCTION", "style", "SOLID");
        // Fórmula de la línea: y = 1.5x - 2
        p.add("Hiperplano de Separación", -10.0, 10.0, 500, x -> 1.5 * x - 2);

        // Clases de puntos (Usamos círculos)
        p.create(new Color(231, 76, 60), "name", "Clase A (Positivos)", "type", "SCATTER", "style", "POINT");  // Rojo
        p.create(new Color(52, 152, 219), "name", "Clase B (Negativos)", "type", "SCATTER", "style", "POINT"); // Azul

        Random rand = new Random(42); // Semilla fija

        // Generamos 300 puntos aleatorios y los clasificamos
        for(int i = 0; i < 300; i++) {
            double px = -10 + rand.nextDouble() * 20; // X entre -10 y 10
            double py = -20 + rand.nextDouble() * 40; // Y entre -20 y 20
            
            // Evaluamos el punto contra la ecuación de la recta
            double boundaryY = 1.5 * px - 2;
            
            // Metemos un margen (+1 / -1) para que no haya puntos justo encima de la línea (Margen del SVM)
            if (py > boundaryY + 1.5) {
                p.add("Clase A (Positivos)", px, py);
            } else if (py < boundaryY - 1.5) {
                p.add("Clase B (Negativos)", px, py);
            }
        }

        p.img(1920, 1080, "./ejemplos/14_linear_classifier.png");
    }
}