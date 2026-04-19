package app;

import ui.Plotter;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {

        Plotter p = new Plotter("Análisis Matemático Múltiple", Plotter.LINE_CHART, "Eje X", "Eje Y");

        // Activamos la cruz central visual y la cuadrícula
        p.crosshair(true, true);
        p.grid(true, true);

        // ==========================================
        // 1. LA PARÁBOLA
        // ==========================================

        // ==========================================
        // 2. LA FUNCIÓN CON ASÍNTOTAS (y = x + 4/x)
        // ==========================================
        // TRUCO DE INGENIERO: Dividimos la función en dos mitades (Izquierda y Derecha de 0).
        // Si evaluáramos de -10 a 10 del tirón, el motor calcularía x=0, daría Infinito,
        // y dibujaría una línea vertical fea cruzando la pantalla para unir los dos lados.

        p.create(new Color(0x1D51DD), "name", "Hipérbola (Izq)", "type", "FUNCTION", "style", "SOLID");
        p.add("Hipérbola (Izq)", -10.0, -0.2, 200, x -> x + (4.0 / x));

        p.create(new Color(0x1D51DD), "name", "Hipérbola (Der)", "type", "FUNCTION", "style", "SOLID");
        p.add("Hipérbola (Der)", 0.2, 10.0, 200, x -> x + (4.0 / x));


        // ==========================================
        // 3. LA ASÍNTOTA OBLICUA (La recta y = x)
        // ==========================================
        p.create(new Color(0x000000), "name", "Asíntota Oblicua (y=x)", "type", "FUNCTION");
        p.add("Asíntota Oblicua (y=x)", -10.0, 10.0, 1000, x -> x);


        // ==========================================
        // 4. LA ASÍNTOTA VERTICAL (La recta x = 0)
        // ==========================================
        // Como las funciones evalúan "Y en base a X", no podemos hacer una línea puramente vertical
        // usando el motor "FUNCTION". Así que usamos el modo línea estándar inyectando dos puntos.
        p.create(new Color(0x999999), "name", "Asíntota Vertical (x=0)", "type", "LINE", "style", "DASHED");



        // Renderizado visual y exportación a disco
        p.plot();
        // p.img("./analisis_matematico.png"); // Descomenta esto para probar el generador headless
    }
}