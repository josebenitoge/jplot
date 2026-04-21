package app;

import ui.Plotter;
import java.awt.Color;

public class MainStackedEmissions {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Evolución de Emisiones de CO2 por Sector", Plotter.STACKED_BAR_CHART, "Año", "Millones de Toneladas (Mt)");
        p.grid(true, true);

        // Paleta de colores cálidos a oscuros para representar contaminación/emisiones
        p.create(new Color(192, 57, 43), "name", "Transporte (Aviación y Terrestre)", "type", "BAR");
        p.create(new Color(211, 84, 0), "name", "Industria Pesada", "type", "BAR");
        p.create(new Color(243, 156, 18), "name", "Generación Eléctrica", "type", "BAR");
        p.create(new Color(127, 140, 141), "name", "Residencial y Comercial", "type", "BAR");

        String[] years = {"2018", "2019", "2020", "2021", "2022", "2023", "2024"};
        
        // Simulamos el "Efecto COVID" en 2020 (caída drástica) y la recuperación posterior
        double[] transporte = {85, 88, 45, 60, 75, 82, 85};
        double[] industria =  {120, 118, 90, 105, 115, 112, 108};
        double[] electrica =  {150, 145, 130, 135, 125, 110, 95}; // Tendencia a la baja real por renovables
        double[] residencial ={40, 42, 48, 45, 43, 41, 40}; // Subida en 2020 por trabajo remoto

        for (int i = 0; i < years.length; i++) {
            // El orden en que añades los datos determinará qué color va abajo y cuál arriba
            p.add("Generación Eléctrica", years[i], electrica[i]);
            p.add("Industria Pesada", years[i], industria[i]);
            p.add("Transporte (Aviación y Terrestre)", years[i], transporte[i]);
            p.add("Residencial y Comercial", years[i], residencial[i]);
        }
        p.plot();
        p.img(1920, 1080, "./etc/20_stacked_emissions.png");
    }
}