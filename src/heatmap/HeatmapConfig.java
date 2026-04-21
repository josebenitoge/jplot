package heatmap;

import model.Config;
import model.Pixel;

import java.awt.*;

public class HeatmapConfig extends Config {

    public HeatmapConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
    }


    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        // 1. Pintar el fondo con el color configurado
        if (backgroundColor != null) {
            for (int x = left; x < right; x++) {
                for (int y = top; y < bottom; y++) {
                    if (x >= 0 && x < pixels.length && y >= 0 && y < pixels[0].length) {
                        pixels[x][y].color = backgroundColor;
                    }
                }
            }
        }

        // 2. Renderizar los nodos encima
        if (getDataset() != null) {
            new HeatmapRenderer(getDataset(), pixels, left, right, top, bottom).bind();
        }
    }
}
