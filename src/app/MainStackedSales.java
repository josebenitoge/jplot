package app;

import ui.Plotter;
import java.awt.Color;

public class MainStackedSales {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Ingresos Trimestrales por Línea de Producto (FY-2024)", Plotter.STACKED_BAR_CHART, "Trimestre", "Millones de Euros (€)");
        p.grid(true, true);

        // Usamos una paleta análoga de azules para mantener la seriedad corporativa
        p.create(new Color(29, 81, 221), "name", "Suscripciones Cloud", "type", "BAR");
        p.create(new Color(52, 152, 219), "name", "Licencias de Software", "type", "BAR");
        p.create(new Color(133, 193, 233), "name", "Soporte Técnico", "type", "BAR");
        p.create(new Color(212, 230, 241), "name", "Hardware", "type", "BAR");

        String[] quarters = {"Q1", "Q2", "Q3", "Q4"};
        
        // Datos simulados (Se apilarán uno encima de otro en el renderizado)
        double[] cloud = {45.2, 51.0, 58.5, 65.0};
        double[] software = {30.5, 28.0, 25.5, 22.0};
        double[] support = {15.0, 15.5, 16.0, 17.5};
        double[] hardware = {10.0, 8.5, 12.0, 25.0}; // Pico en Q4 por ventas de fin de año

        for (int i = 0; i < quarters.length; i++) {
            p.add("Suscripciones Cloud", quarters[i], cloud[i]);
            p.add("Licencias de Software", quarters[i], software[i]);
            p.add("Soporte Técnico", quarters[i], support[i]);
            p.add("Hardware", quarters[i], hardware[i]);
        }
        p.plot();
        p.img(1920, 1080, "./etc/18_stacked_sales.png");
    }
}