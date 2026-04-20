package model;

import java.awt.*;
import java.util.LinkedList;

/**
 * Representa una secuencia lógica y cohesiva de datos dentro de un gráfico.
 * Una serie agrupa un conjunto de puntos, una identidad visual (color)
 * y una etiqueta descriptiva (nombre).
 * * Es la unidad básica que el motor de renderizado utiliza para aplicar
 * estilos específicos (como líneas sólidas, barras o burbujas) a un subconjunto
 * de datos del Dataset.
 */
public class Series {

    private final String name;
    private final Color color;

    /**
     * Almacén de puntos de datos. Se utiliza una {@link LinkedList} para optimizar
     * las operaciones de inserción frecuente al final de la lista durante la
     * fase de recolección de datos, manteniendo un rendimiento constante independientemente
     * del volumen de puntos.
     */
    private final LinkedList<Point> dataPoints = new LinkedList<>();

    /**
     * Construye una nueva serie con una identidad visual y descriptiva definida.
     *
     * @param name  El nombre único de la serie, utilizado en la leyenda y tooltips.
     * @param color El color asignado para el renderizado de todos los puntos de esta serie.
     */
    public Series(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Registra un nuevo punto matemático en la cronología o conjunto de esta serie.
     *
     * @param dataPoint El objeto {@link Point} que contiene las coordenadas a almacenar.
     */
    public void add(Point dataPoint){
        dataPoints.add(dataPoint);
    }

    /**
     * Recupera el color representativo de la serie.
     *
     * @return El objeto {@link Color} utilizado para el dibujado.
     */
    public Color color() { return color; }

    /**
     * Recupera el nombre de la serie.
     *
     * @return Cadena de texto con el identificador de la serie.
     */
    public String name() { return name; }

    /**
     * Proporciona acceso a la lista completa de puntos de datos para su procesamiento
     * por parte de los motores de renderizado.
     *
     * @return Una {@link LinkedList} con todos los objetos {@link Point} registrados.
     */
    public LinkedList<Point> points() { return dataPoints; }
}