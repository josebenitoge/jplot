package app;

import ui.Plotter;
import java.awt.Color;

public class MainStackedTraffic {
    public static void main(String[] args) {
        
        Plotter p = new Plotter("Monitorización de Peticiones HTTP (Últimas 24h)", Plotter.STACKED_BAR_CHART, "Hora del Día", "Número de Peticiones (Miles)");
        p.grid(false, true);

        // Los colores tienen significado semántico (Verde = OK, Amarillo = Aviso, Rojo = Error)
        p.create(new Color(46, 204, 113), "name", "2xx (Éxito)", "type", "BAR");
        p.create(new Color(241, 196, 15), "name", "3xx (Redirecciones)", "type", "BAR");
        p.create(new Color(230, 126, 34), "name", "4xx (Errores Cliente)", "type", "BAR");
        p.create(new Color(231, 76, 60), "name", "5xx (Errores Servidor)", "type", "BAR");

        // Simulamos un pico de tráfico a las 14:00 con caída de servidor (aumento de 5xx)
        for (int hora = 8; hora <= 18; hora++) {
            String timeLabel = hora + ":00";
            
            // Tráfico base normal
            double baseTraffic = 50 + (hora == 14 ? 150 : 0); // Pico a las 14:00
            
            p.add("2xx (Éxito)", timeLabel, baseTraffic * 0.85);
            p.add("3xx (Redirecciones)", timeLabel, baseTraffic * 0.10);
            p.add("4xx (Errores Cliente)", timeLabel, baseTraffic * 0.04);
            
            // Si es la hora pico, los errores de servidor se disparan
            double serverErrors = (hora == 14) ? baseTraffic * 0.25 : baseTraffic * 0.01;
            p.add("5xx (Errores Servidor)", timeLabel, serverErrors);
        }
        p.plot();
        p.img(1920, 1080, "./etc/19_stacked_server_traffic.png");
    }
}