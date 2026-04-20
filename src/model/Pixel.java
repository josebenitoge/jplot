package model;

import java.awt.*;

/**
 * Estructura de datos elemental que representa un único punto (píxel) dentro de la
 * matriz bidimensional de renderizado del motor jPlot.
 * * Nota de arquitectura: Los atributos son públicos por diseño. Al ser la unidad
 * base de renderizado, el acceso directo a la memoria (pixels[x][y].color) evita
 * la sobrecarga de la pila de llamadas (overhead) que generarían miles de millones
 * de invocaciones a métodos getter/setter durante el repintado o rasterizado de funciones.
 */
public class Pixel {

    /** Coordenada horizontal absoluta dentro del lienzo de renderizado. */
    public int x;

    /** Coordenada vertical absoluta dentro del lienzo de renderizado. */
    public int y;

    /** El color asignado a este píxel en el búfer actual. */
    public Color color;

    /**
     * Construye un nuevo píxel virtual en memoria.
     *
     * @param x     La posición X (columna) en la matriz de la pantalla.
     * @param y     La posición Y (fila) en la matriz de la pantalla.
     * @param color El color inicial del píxel (usualmente Color.WHITE para limpiar el fondo).
     */
    public Pixel(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
}