package app;

import ui.Plotter;
import java.awt.Color;

public class MainBarBenchmark {
    public static void main(String[] args) {
        Plotter p = new Plotter("Rendimiento Multi-Núcleo de Procesadores", Plotter.BAR_CHART, "Arquitectura", "Puntuación (Cinebench)");
        p.grid(true, true);
        // Creamos una única serie con un azul técnico
        p.create(new Color(0x2B5B84), "name", "Puntuación", "type", "BAR");
        // Añadimos datos categóricos
        p.add("Puntuación", "Ryzen 9 7950X", 38500);
        p.add("Puntuación", "Core i9 14900K", 36200);
        p.add("Puntuación", "Apple M3 Max", 24000);
        p.add("Puntuación", "Snapdragon X Elite", 15300);
        //p.plot();
        // Exportamos en Full HD
        p.img(1920, 1080, "./ejemplos/01_bar_benchmark.png");
    }
}