package model;

/**
 * Estructura de datos inmutable que representa un punto matemático bidimensional
 * en el espacio de coordenadas de la gráfica.
 * * A diferencia de la clase {@link Pixel} (que representa coordenadas enteras de
 * pantalla para el rasterizado final), esta clase almacena los valores flotantes
 * reales (dominio y rango) introducidos originalmente por el usuario.
 */
public class Point {

    private final double x, y;

    /**
     * Construye un nuevo punto de datos matemático inmutable.
     *
     * @param x El valor numérico en el eje de abscisas (Dominio / Eje X).
     * @param y El valor numérico en el eje de ordenadas (Rango / Eje Y).
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Recupera el valor del dominio de este punto.
     *
     * @return La coordenada matemática exacta en el eje X.
     */
    public double getX() { return x; }

    /**
     * Recupera el valor del rango de este punto.
     *
     * @return La coordenada matemática exacta en el eje Y.
     */
    public double getY() { return y; }
}