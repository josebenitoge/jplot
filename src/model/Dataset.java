package model;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Estructura de datos abstracta base para el motor de jPlot.
 * Actúa como un contenedor centralizado que almacena, agrupa y calcula dinámicamente
 * los límites matemáticos (bounding box) de todas las series de datos registradas.
 */
public abstract class Dataset {

    /**
     * Almacén principal de las series. Se utiliza un LinkedHashMap para preservar
     * estrictamente el orden de inserción. Esto garantiza que el orden en el que el usuario
     * añade las series sea el mismo orden en el que se apilan visualmente (Z-Index)
     * y el mismo orden en el que aparecen en la leyenda.
     */
    protected final LinkedHashMap<String, Series> series = new LinkedHashMap<>();

    // Variables de estado para calcular la caja delimitadora (Bounding Box) global del gráfico
    protected double minX = Double.MAX_VALUE;
    protected double maxX = -Double.MAX_VALUE;
    protected double minY = Double.MAX_VALUE;
    protected double maxY = -Double.MAX_VALUE;

    /** Generador interno para utilidades aleatorias (como auto-asignación de colores). */
    protected static final Random rand = new Random();

    /**
     * Inicializa y registra una nueva serie de datos en la colección.
     *
     * @param color  El color base asignado a la serie para su renderizado.
     * @param config Pares clave-valor dinámicos para configurar la serie (ej. estilo, opacidad).
     */
    public abstract void create(Color color, String... config);

    /**
     * Añade una tupla de datos puramente numéricos a una serie existente.
     *
     * @param series Nombre de la serie destino.
     * @param x      Vector numérico de n-dimensiones (X, Y) o (X, Y, Radio).
     */
    public abstract void add(String series, double... x);

    /**
     * Método gancho (hook) para añadir datos con un eje categórico.
     * Posee una implementación vacía por defecto para no romper el Principio de Segregación
     * de Interfaces (ISP). Así, las clases hijas puramente matemáticas (como las de Líneas o Burbujas)
     * no se ven forzadas a implementarlo, mientras que los gráficos de Barras o Sectores pueden sobrescribirlo.
     *
     * @param series   Nombre de la serie destino.
     * @param category Etiqueta textual del eje (ej. "Trimestre 1").
     * @param value    Magnitud numérica asociada a la categoría.
     */
    public void add(String series, String category, double value) {}

    /**
     * Evalúa y actualiza dinámicamente los límites matemáticos globales del lienzo.
     * Debe ser invocado por las clases hijas cada vez que se inyecta un nuevo punto válido.
     *
     * @param x Coordenada espacial X evaluada.
     * @param y Coordenada espacial Y evaluada.
     */
    protected void updateLimits(double x, double y) {
        if (x > maxX) maxX = x;
        if (x < minX) minX = x;
        if (y > maxY) maxY = y;
        if (y < minY) minY = y;
    }

    /**
     * Recupera la colección completa de series registradas.
     *
     * @return Mapa de series indexadas por su nombre.
     */
    public HashMap<String, Series> series() {
        return series;
    }

    /** @return El valor máximo registrado en el dominio espacial horizontal. */
    public double getMaxX() { return maxX; }

    /** @return El valor máximo registrado en el rango espacial vertical. */
    public double getMaxY() { return maxY; }

    /** @return El valor mínimo registrado en el dominio espacial horizontal. */
    public double getMinX() { return minX; }

    /** @return El valor mínimo registrado en el rango espacial vertical. */
    public double getMinY() { return minY; }

    /**
     * Genera un color pseudoaleatorio optimizado para visualización de datos.
     * Mantiene una saturación y brillo controlados mediante el espacio de color HSB
     * para evitar tonos demasiado oscuros, apagados o invisibles contra un fondo blanco.
     *
     * @return Una instancia de Color apta para renderizado de series.
     */
    protected Color randomColor() {
        float hue = rand.nextFloat();
        float saturation = 0.3f + (rand.nextFloat() * 0.3f); // Saturación entre 30% y 60%
        float brightness = 0.6f + (rand.nextFloat() * 0.3f); // Brillo entre 60% y 90%
        return Color.getHSBColor(hue, saturation, brightness);
    }
}