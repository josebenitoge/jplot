package app;

import ui.Plotter;
import java.awt.Color;

public class MainBarDemographics {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Distribución Demográfica de Usuarios Activos", Plotter.BAR_CHART, "Rango de Edad", "Millones de Usuarios");
        p.grid(false, true); // Solo líneas horizontales para facilitar la lectura de la altura

        // Color naranja/coral
        p.create(new Color(0xE65100), "name", "Usuarios Activos", "type", "BAR");

        p.add("Usuarios Activos", "13-17", 12.5);
        p.add("Usuarios Activos", "18-24", 45.2);
        p.add("Usuarios Activos", "25-34", 68.7);
        p.add("Usuarios Activos", "35-44", 32.1);
        p.add("Usuarios Activos", "45-54", 18.4);
        p.add("Usuarios Activos", "55+", 9.3);

        p.plot();
        p.img(1920, 1080, "./etc/02_bar_demographics.png");
    }
}