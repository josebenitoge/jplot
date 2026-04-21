package app;

import ui.Plotter;
import java.awt.Color;

public class MainPolyline {

    public static void main(String[] args) {
        // 1. Inicializamos la fachada
        Plotter p = new Plotter("Comparativa de Evolución", Plotter.POLYLINE_PLOT, "Días", "Valor");

        // Fondo gris claro
        p.setBackground(new Color(235, 236, 240));

        // ==========================================
        // SERIE 1: AZUL (Producto A)
        // ==========================================
        p.create(new Color(0, 85, 140), "name", "Producto A", "marker", "CIRCLE");

        p.add("Producto A", "Thu 1", 100.2);
        p.add("Producto A", "Fri 1", 108.3);
        p.add("Producto A", "Mon 2", 109.5);
        p.add("Producto A", "Tue 2", 104.9);
        p.add("Producto A", "Wed 2", 106.0);
        p.add("Producto A", "Thu 2", 107.9);
        p.add("Producto A", "Fri 2", 106.1);
        p.add("Producto A", "Mon 3", 102.0);
        p.add("Producto A", "Tue 3", 102.4);

        // ==========================================
        // SERIE 2: ROJA (Producto B)
        // ==========================================
        // Usamos un rojo corporativo/suave para mantener la estética
        p.create(new Color(220, 60, 60), "name", "Producto B", "marker", "CIRCLE");

        p.add("Producto B", "Thu 1", 105.0);
        p.add("Producto B", "Fri 1", 104.0);
        p.add("Producto B", "Mon 2", 102.5); // Intersección (cae por debajo del azul)
        p.add("Producto B", "Tue 2", 108.0); // Intersección (sube por encima)
        p.add("Producto B", "Wed 2", 110.5);
        p.add("Producto B", "Thu 2", 105.0);
        p.add("Producto B", "Fri 2", 103.0);
        p.add("Producto B", "Mon 3", 106.0);
        p.add("Producto B", "Tue 3", 108.5);

        // 4. Renderizado final
        p.plot();
        p.img(1920, 1080, "./etc/25_polyline.png");
    }
}