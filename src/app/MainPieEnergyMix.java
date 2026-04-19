package app;

import ui.Plotter;
import java.awt.Color;

public class MainPieEnergyMix {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Generación Eléctrica Nacional por Fuente (TWh)", Plotter.PIE_CHART, "", "");
        p.grid(false, false);

        // Energías Renovables (Tonos fríos y verdes)
        p.create(new Color(46, 204, 113), "name", "Eólica", "type", "PIE");
        p.create(new Color(241, 196, 15), "name", "Solar Fotovoltaica", "type", "PIE");
        p.create(new Color(52, 152, 219), "name", "Hidráulica", "type", "PIE");
        
        // Energías Base / No Renovables (Tonos cálidos y neutros)
        p.create(new Color(142, 68, 173), "name", "Nuclear", "type", "PIE");
        p.create(new Color(230, 126, 34), "name", "Ciclo Combinado (Gas)", "type", "PIE");
        p.create(new Color(90, 90, 90), "name", "Carbón", "type", "PIE");

        // Valores absolutos (Teravatios-hora). El gráfico los convierte a 360 grados.
        p.add("Eólica", "Generación", 61.2);
        p.add("Nuclear", "Generación", 58.3);
        p.add("Ciclo Combinado (Gas)", "Generación", 45.7);
        p.add("Solar Fotovoltaica", "Generación", 32.4);
        p.add("Hidráulica", "Generación", 25.1);
        p.add("Carbón", "Generación", 4.2);

        p.img(1920, 1080, "./ejemplos/16_pie_energy_mix.png");
    }
}