package app;

import ui.Plotter;
import java.awt.Color;

public class MainBarFinancial {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Evolución de Ingresos Trimestrales", Plotter.BAR_CHART, "Trimestre (Quarter)", "Ingresos Brutos (M€)");
        p.grid(true, true);

        // Creamos dos series de datos para comparar
        p.create(new Color(0x999999), "name", "Año Anterior", "type", "BAR"); // Gris neutral
        p.create(new Color(0x1D51DD), "name", "Año Actual", "type", "BAR");   // Azul corporativo destacado

        // Quarter 1
        p.add("Año Anterior", "Q1", 110.5);
        p.add("Año Actual", "Q1", 125.0);

        // Quarter 2
        p.add("Año Anterior", "Q2", 115.0);
        p.add("Año Actual", "Q2", 132.4);

        // Quarter 3
        p.add("Año Anterior", "Q3", 98.2);
        p.add("Año Actual", "Q3", 105.8);

        // Quarter 4
        p.add("Año Anterior", "Q4", 145.0);
        p.add("Año Actual", "Q4", 168.9);

        p.plot();
        p.img(1920, 1080, "./etc/03_bar_financial.png");
    }
}