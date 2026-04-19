package bubble;

import model.Config;
import model.Pixel;

public class BubbleConfig extends Config {

    public BubbleConfig(String xTitle, String yTitle) {
        super(xTitle, yTitle);
    }

    @Override
    public void bind(Pixel[][] pixels, int left, int right, int top, int bottom) {
        if (getDataset() != null) {
            new BubbleRenderer(getDataset(), pixels, left, right, top, bottom).bind();
        }
    }
}
