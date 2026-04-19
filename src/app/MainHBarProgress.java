package app;

import ui.Plotter;
import java.awt.Color;

public class MainHBarProgress {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Auditoría de Seguridad: Estado por Departamentos", Plotter.HBAR_CHART, "Progreso de Certificación (%)", "Departamento");
        p.grid(true, false);

        // Usamos un color verde (éxito/progreso)
        p.create(new Color(39, 174, 96), "name", "Completado", "type", "BAR");

        p.add("Completado", "Recursos Humanos", 100.0);
        p.add("Completado", "Operaciones IT", 85.5);
        p.add("Completado", "Desarrollo Backend", 72.0);
        p.add("Completado", "Atención al Cliente", 45.0);
        p.add("Completado", "Ventas Externas", 12.5);

        p.img(1920, 1080, "./ejemplos/10_hbar_progress.png");
    }
}