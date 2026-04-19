package line;

import java.awt.Color;

public class LineDataset extends model.Dataset {

    public static final String TYPE_DOT_SET = "DOT_SET";
    public static final String TYPE_FUNCTION = "FUNCTION";

    public static final String STYLE_SOLID = "SOLID";
    public static final String STYLE_DASHED = "DASHED";
    public static final String STYLE_CIRCLE = "CIRCLE";
    public static final String STYLE_CROSS = "CROSS";
    public static final String STYLE_TRIANGLE = "TRIANGLE";

    private String getDefaultStyle(String type) {
        if (TYPE_FUNCTION.equals(type)) {
            return STYLE_SOLID;
        } else {
            return STYLE_CIRCLE;
        }
    }

    @Override
    public void create(Color color, String... config) {
        // 1. Validamos que vengan en pares (Clave, Valor)
        if (config.length % 2 != 0) {
            System.err.println("Error: 'create' exige argumentos en pares clave-valor. Ejemplo: \"name\", \"MiSerie\"");
            return;
        }

        // 2. Valores por defecto por si el usuario no manda alguno
        String name = "Serie_" + System.currentTimeMillis(); // Nombre fallback
        String type = TYPE_DOT_SET;
        String style = null;

        // 3. Recorremos el varargs saltando de 2 en 2
        for (int i = 0; i < config.length; i += 2) {
            String key = config[i].toLowerCase(); // Convertimos a minúscula por seguridad
            String value = config[i + 1];

            switch (key) {
                case "name":
                    name = value;
                    break;
                case "type":
                    type = value;
                    break;
                case "style":
                    style = value;
                    break;
                default:
                    System.err.println("Advertencia: Clave desconocida '" + key + "'. Se ignorará.");
            }
        }

        // 4. Lógica final de estilos y colores
        if (style == null) {
            style = getDefaultStyle(type);
        }

        Color finalColor = (color != null) ? color : randomColor();

        // 5. Guardamos la serie en el HashMap protegido del padre
        series.put(name, new LineSeries(name, type, finalColor, style));
    }
    @Override
    public void add(String seriesName, double... x) {
        // Para líneas necesitamos exactamente X e Y (al menos 2 valores)
        if (x.length < 2) return;

        double px = x[0];
        double py = x[1];

        // Usamos el método protected del padre para actualizar límites matemáticos
        updateLimits(px, py);

        if (series.containsKey(seriesName)) {
            DataPoint dataPoint = new DataPoint(px, py);
            series.get(seriesName).add(dataPoint);
        } else {
            System.err.println("Advertencia: El gráfico '" + seriesName + "' no existe. Crea el envoltorio primero.");
        }
    }

}