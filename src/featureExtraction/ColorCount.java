package featureExtraction;

import java.awt.image.BufferedImage;

import main.Color;
import main.FeatureExtractor;
import main.Util;

public class ColorCount implements FeatureExtractor {

    private static final int RANGE = 30;

    private final Color color;

    public ColorCount(Color color) {
        this.color = color;
    }

    @Override
    public int extractFeature(BufferedImage image) {
        int colorCount = 0;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                Color color = Util.getColorOfPixel(rgb);
                if (color.equals(this.color))
                    colorCount++;
            }
        }
        double portion = (double) colorCount / (image.getWidth() * image.getHeight());
        return (int) (portion * RANGE);
    }

}