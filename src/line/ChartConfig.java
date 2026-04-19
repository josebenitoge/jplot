package line;

import model.Config;
import model.Pixel;

public class ChartConfig extends Config {

    public ChartConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (getDataset() != null) {
            new LineRenderer((LineDataset) getDataset(), pixels, left, right, top, bottom).bind();
        }
    }
}