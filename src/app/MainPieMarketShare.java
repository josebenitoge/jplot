package app;

import ui.Plotter;
import java.awt.Color;

public class MainPieMarketShare {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Cuota de Mercado de SO Móviles (2024)", Plotter.PIE_CHART, "", "");
        // En un Pie Chart normalmente apagamos la cuadrícula
        p.grid(false, false); 

        // Definimos cada porción con su color de marca aproximado
        p.create(new Color(34, 96, 227), "name", "Android", "type", "PIE");
        p.create(new Color(250, 203, 19), "name", "iOS", "type", "PIE");
        p.create(new Color(39, 204, 124), "name", "HarmonyOS", "type", "PIE");
        p.create(new Color(218, 55, 55), "name", "Otros", "type", "PIE");

        // Añadimos los porcentajes (El motor debería calcular el total y sacar los ángulos)
        p.add("Android", "Usuarios", 70.1);
        p.add("iOS", "Usuarios", 27.2);
        p.add("HarmonyOS", "Usuarios", 2.0);
        p.add("Otros", "Usuarios", 0.7);
        p.plot();
        p.img(1920, 1080, "./etc/15_pie_market_share.png");
    }
}