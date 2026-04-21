package stem;

import java.awt.Color;

public class StemDataset extends model.Dataset {

    public static final String STYLE_CIRCLE_SOLID = "CIRCLE_SOLID";
    public static final String STYLE_CIRCLE_HOLLOW = "CIRCLE_HOLLOW";
    public static final String STYLE_SQUARE_SOLID = "SQUARE_SOLID";
    public static final String STYLE_SQUARE_HOLLOW = "SQUARE_HOLLOW";
    public static final String STYLE_LOZENGE_SOLID = "LOZENGE_SOLID";
    public static final String STYLE_LOZENGE_HOLLOW = "LOZENGE_HOLLOW";

    @Override
    public void create(Color color, String... config) {
        if (config.length % 2 != 0) {
            System.err.println("Error: 'create' exige argumentos en pares clave-valor. Ejemplo: \"name\", \"MiSerie\"");
            return;
        }

        String name = "StemSerie_" + System.currentTimeMillis();
        String style = STYLE_CIRCLE_SOLID;

        for (int i = 0; i < config.length; i += 2) {
            String key = config[i].toLowerCase();
            String value = config[i + 1];

            switch (key) {
                case "name":
                    name = value;
                    break;
                case "style":
                    style = value.toUpperCase();
                    break;
                default:
                    System.err.println("Advertencia: Clave desconocida '" + key + "'. Se ignorará.");
            }
        }

        Color finalColor = (color != null) ? color : randomColor();
        series.put(name, new StemSeries(name, finalColor, style));
    }

    @Override
    public void add(String seriesName, double... x) {
        if (x.length < 2) return;

        double px = x[0];
        double py = x[1];

        // Mantiene el 0 siempre visible para la línea base
        updateLimits(px, py);
        updateLimits(px, 0.0);

        if (series.containsKey(seriesName)) {
            StemPoint stemPoint = new StemPoint(px, py);
            series.get(seriesName).add(stemPoint);
        } else {
            System.err.println("Advertencia: El gráfico '" + seriesName + "' no existe. Crea el envoltorio primero.");
        }
    }
}