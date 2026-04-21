package confusion;

import model.Dataset;
import model.Series;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfusionDataset extends Dataset {

    private final List<String> classes = new ArrayList<>();
    private final Map<String, Map<String, Double>> matrix = new LinkedHashMap<>();

    @Override
    public void create(Color color, String... config) {
        // Creamos la serie interna. Usamos "Matrix" como clave por defecto.
        Series s = new Series("Matrix", color);
        series.put("Matrix", s);
    }

    // ESTE ES EL MÉTODO QUE USA LA FACHADA
    @Override
    public void add(String actual, String predicted, double value) {
        if (!classes.contains(actual)) classes.add(actual);
        if (!classes.contains(predicted)) classes.add(predicted);

        matrix.computeIfAbsent(actual, k -> new LinkedHashMap<>()).put(predicted, value);

        // Actualizamos límites ficticios para que el motor no de error
        updateLimits(0, 0);
    }

    @Override
    public void add(String series, double... x) { /* No se usa */ }

    public List<String> getClasses() { return classes; }

    public double getValue(String actual, String predicted) {
        return matrix.getOrDefault(actual, Map.of()).getOrDefault(predicted, 0.0);
    }

    public double getMaxValue() {
        return matrix.values().stream()
                .flatMap(m -> m.values().stream())
                .max(Double::compare).orElse(1.0);
    }
}