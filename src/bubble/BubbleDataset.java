package bubble;

import model.Dataset;

import java.awt.Color;

// OJO: Asumo que actualizaste model.Dataset para que sea genérico <T>.
// Si no lo hiciste, quita el <Series> y haz cast luego, pero lo ideal es model.Dataset<Series>
public class BubbleDataset extends Dataset {

    @Override
    public void create(Color color, String... config) {
        if (config.length % 2 != 0) {
            System.err.println("Error: 'create' exige pares clave-valor.");
            return;
        }

        String name = "Bubble_" + System.currentTimeMillis();

        for (int i = 0; i < config.length; i += 2) {
            String key = config[i].toLowerCase();
            String value = config[i + 1];

            if (key.equals("name")) {
                name = value;
            }
        }

        Color finalColor = (color != null) ? color : randomColor();

        // Guardamos en el HashMap del padre
        series().put(name, new BubbleSeries(name, finalColor));
    }

    @Override
    public void add(String seriesName, double... x) {
        // Exigimos 3 parámetros: X, Y, y Z (Tamaño)
        if (x.length < 3) {
            System.err.println("Error: El gráfico de burbujas requiere X, Y, y Z (Tamaño).");
            return;
        }

        double px = x[0];
        double py = x[1];
        double pz = x[2]; // ¡Nuestro tamaño!

        updateLimits(px, py); // Matemáticas del padre

        if (series().containsKey(seriesName)) {
            BubblePoint dataPoint = new BubblePoint(px, py, pz);
            series().get(seriesName).add(dataPoint);
        } else {
            System.err.println("Advertencia: El gráfico '" + seriesName + "' no existe.");
        }
    }
}