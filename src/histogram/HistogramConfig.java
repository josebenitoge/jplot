package histogram;

import model.Config;
import model.Pixel;
import java.awt.Color;

public class HistogramConfig extends Config {

    public HistogramConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);

    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        HistogramDataset ds = (HistogramDataset) getDataset();
        
        if (ds != null) {
            // 1. Compilamos los datos crudos en contenedores de frecuencia
            ds.compileData();

            // 3. Ejecutamos el renderizador
            new HistogramRenderer(ds, pixels, left, right, top, bottom).bind();
        }
    }
}