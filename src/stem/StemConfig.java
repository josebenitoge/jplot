package stem;

import model.Config;
import model.Pixel;

public class StemConfig extends Config {

    public StemConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (getDataset() != null) {
            new StemRenderer((StemDataset) getDataset(), pixels, left, right, top, bottom).bind();
        }
    }
}