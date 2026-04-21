package app;

import ui.Plotter;
import multi.MultiPlotter;
import java.awt.Color;

public class MainDashboard {
    public static void main(String[] args) {
        
        // 1. Gráfico de Líneas (Matemático)
        Plotter p1 = new Plotter("Señales", Plotter.LINE_CHART, "Tiempo", "Voltaje");
        p1.create(Color.BLUE, "name", "Seno", "type", "FUNCTION");
        p1.add("Seno", 0, 10, 500, Math::sin);

        // 2. Gráfico de Barras
        Plotter p2 = new Plotter("Ventas", Plotter.BAR_CHART, "Mes", "Euros");
        p2.create(Color.ORANGE, "name", "2024");
        p2.add("2024", "Enero", 1200);
        p2.add("2024", "Febrero", 1800);

        // 3. Gráfico de Sectores
        Plotter p3 = new Plotter("Navegadores", Plotter.PIE_CHART, "", "");
        p3.create(Color.RED, "name", "Chrome");
        p3.create(Color.BLUE, "name", "Firefox");
        p3.add("Chrome", "Global", 65);
        p3.add("Firefox", "Global", 10);

        // 4. Gráfico de Burbujas
        Plotter p4 = new Plotter("Análisis Cluster", Plotter.BUBBLE_CHART, "X", "Y");
        p4.create(new Color(46, 204, 113, 150), "name", "Nube");
        for(int i=0; i<50; i++) p4.add("Nube", Math.random()*10, Math.random()*10, Math.random()*20);

        // --- COMPOSICIÓN DEL DASHBOARD ---
        MultiPlotter dashboard = new MultiPlotter("Panel de Control de Ingeniería", 2, 2);
        dashboard.add(p1);
        dashboard.add(p2);
        dashboard.add(p3);
        dashboard.add(p4);

        // Ver en ventana y guardar en 4K
        dashboard.plot();
        dashboard.img(1080, 720, "etc/21_full_dashboard.png");
    }
}