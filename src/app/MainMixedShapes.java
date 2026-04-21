package app;

import ui.Plotter;
import java.awt.Color;

public class MainMixedShapes {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Análisis Geométrico Multicapa", Plotter.LINE_CHART, "Eje X", "Eje Y");
        p.grid(true, true);

        // 1. Función Parabólica (Sólida)
        p.create(new Color(0, 0, 0, 255), "name", "Parábola", "type", "FUNCTION", "style", "SOLID");
        p.add("Parábola", -5.0, 5.0, 500, x -> 0.5 * Math.pow(x, 2) - 5);

        // 2. Función Lineal (Punteada)
        p.create(new Color(48, 120, 16), "name", "Recta Secante", "type", "FUNCTION", "style", "DASHED");
        p.add("Recta Secante", -5.0, 5.0, 200, x -> -x + 2);

        // 3. Puntos Sueltos: TRIÁNGULOS (Usamos type "SCATTER" para que no los una con líneas)
        p.create(new Color(220, 29, 58), "name", "Vértices", "type", "SCATTER", "style", "TRIANGLE");
        p.add("Vértices", -2.0, 4.5);
        p.add("Vértices", 3.12, 1.5);
        p.add("Vértices", 3.72, -1.0);

        // 4. Puntos Sueltos: CRUCES
        p.create(new Color(34, 65, 250), "name", "Intersecciones", "type", "SCATTER", "style", "CROSS");
        p.add("Intersecciones", -3.8, 3.8);
        p.add("Intersecciones", 2.2, -0.2);

        // 5. Puntos Sueltos: CÍRCULOS (El default)
        p.create(new Color(248, 181, 11), "name", "Focos", "type", "SCATTER", "style", "POINT");
        p.add("Focos", 0.0, -2.5);
        p.add("Focos", 0.0, 0.0);
        p.plot();
        p.img(1920, 1080, "./etc/12_mixed_shapes.png");
    }
}