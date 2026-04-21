package app;

import ui.Plotter;
import java.awt.Color;

public class MainHBarComparative {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Satisfacción del Cliente por Región Geográfica", Plotter.HBAR_CHART, "Número de Encuestas", "Región Comercial");
        p.grid(true, false);

        // Creamos dos series: Positivas y Negativas
        p.create(new Color(46, 204, 113), "name", "Satisfecho", "type", "BAR"); // Verde
        p.create(new Color(231, 76, 60), "name", "Insatisfecho", "type", "BAR"); // Rojo

        // EMEA (Europa, Oriente Medio, África)
        p.add("Satisfecho", "EMEA", 4500);
        p.add("Insatisfecho", "EMEA", 1200);

        // LATAM (Latinoamérica)
        p.add("Satisfecho", "LATAM", 3200);
        p.add("Insatisfecho", "LATAM", 850);

        // APAC (Asia-Pacífico)
        p.add("Satisfecho", "APAC", 5100);
        p.add("Insatisfecho", "APAC", 2300);

        // NA (Norteamérica)
        p.add("Satisfecho", "Norteamérica", 6800);
        p.add("Insatisfecho", "Norteamérica", 950);
        p.plot();
        p.img(1920, 1080, "./etc/11_hbar_comparative.png");
    }
}