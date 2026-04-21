package app;

import ui.Plotter;
import java.awt.Color;
import java.util.Random;

public class MainBMH {
    public static void main(String[] args) {
        // 1. Inicialización
        Plotter p = new Plotter("'bmh' style sheet", Plotter.HISTOGRAM_PLOT, "", "");

        // Fondo grisáceo clásico de Matplotlib BMH
        p.setBackground(new Color(210, 210, 210));
        
        // Habilitamos la cuadrícula (como en la imagen)


        // 2. Definición de colores con Transparencia (Alpha = 200 de 255)
        Color cGreen  = new Color(76, 114, 50, 200);
        Color cRed    = new Color(153, 24, 44, 200);
        Color cBlue   = new Color(51, 122, 163, 200);
        Color cPurple = new Color(106, 92, 135, 200);

        // 3. Creación de series ("stroke", "false" elimina los bordes negros)
        p.create(cGreen,  "name", "D1", "bins", "25", "stroke", "false");
        p.create(cRed,    "name", "D2", "bins", "25", "stroke", "false");
        p.create(cBlue,   "name", "D3", "bins", "25", "stroke", "false");
        p.create(cPurple, "name", "D4", "bins", "25", "stroke", "false");

        // 4. Inyección de datos crudos simulando las 4 campanas de Gauss
        Random rand = new Random();
        for (int i = 0; i < 200; i++) {
            // Verde: A la izquierda, muy estrecha
            p.add("D1", (rand.nextGaussian() * 0.03) + 0.1); 
            
            // Rojo: Medio-izquierda, dispersión media
            p.add("D2", (rand.nextGaussian() * 0.1) + 0.25); 
            
            // Azul: Centro, dispersión media-alta
            p.add("D3", (rand.nextGaussian() * 0.08) + 0.5); 
            
            // Morado: A la derecha, estrecha
            p.add("D4", (rand.nextGaussian() * 0.05) + 0.8); 
        }

        // 5. Renderizado final
        p.plot();
        System.out.println("Histograma Multiserie Renderizado. Se debe apreciar la transparencia.");
    }
}