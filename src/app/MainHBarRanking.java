package app;

import ui.Plotter;
import java.awt.Color;

public class MainHBarRanking {
    public static void main(String[] args) {
        
        // Fíjate que en HBAR, el Eje X es el numérico (abajo) y el Eje Y es el categórico (izquierda)
        Plotter p = new Plotter("Lenguajes de Programación Más Demandados (2024)", Plotter.HBAR_CHART, "Ofertas de Empleo (Miles)", "Lenguaje / Tecnología");
        p.grid(true, false); // Las líneas verticales ayudan a leer la longitud de la barra horizontal

        // Usamos un color morado tecnológico
        p.create(new Color(142, 68, 173), "name", "Ofertas", "type", "BAR"); 

        // Los añadimos ordenados de menor a mayor para que el motor los apile correctamente
        p.add("Ofertas", "C / C++", 85.4);
        p.add("Ofertas", "C# / .NET", 112.0);
        p.add("Ofertas", "TypeScript", 145.2);
        p.add("Ofertas", "Java (Enterprise)", 189.5);
        p.add("Ofertas", "JavaScript / Node", 210.8);
        p.add("Ofertas", "Python (Data/AI)", 254.3);

        p.img(1920, 1080, "./ejemplos/09_hbar_ranking.png");
    }
}