package featureExtraction;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import main.Color;
import main.FeatureExtractor;
import main.Util;

public class ColorCount implements FeatureExtractor {

    private static Map<BufferedImage, BufferedImage[]> calculatedQuadrants = new HashMap<>();

    private final int quadrant;

    private final Color color;

    public ColorCount(int quadrant, Color color) {
        this.quadrant = quadrant;
        this.color = color;
    }

    @Override
    public int extractFeature(BufferedImage image) {
        BufferedImage[] quadrants = divideImageIntoQuadrants(image);
        return (int) getColorCount(quadrants[this.quadrant]);
    }

    private double getColorCount(BufferedImage quadrant) {
        int unknownCount = 0;
        int colorCount = 0;
        for (int i = 0; i < quadrant.getWidth(); i++) {
            for (int j = 0; j < quadrant.getHeight(); j++) {
                int rgb = quadrant.getRGB(i, j);
                Color color = Util.getColorOfPixel(rgb);
                if (color.equals(this.color))
                    colorCount++;
                else if (color.equals(Color.UNKNOWN))
                    unknownCount++;
            }
        }
        return colorCount; // Normalizing by division with unknon count
    }

    private BufferedImage[] divideImageIntoQuadrants(BufferedImage image) {
        BufferedImage[] storedQuadrants = calculatedQuadrants.get(image);
        if (storedQuadrants != null)
            return storedQuadrants;
        
        int halfWidth = image.getWidth() / 2;
        int halfHeight = image.getHeight() / 2;

        BufferedImage[] quadrants = new BufferedImage[4];
        quadrants[0] = image.getSubimage(0, 0, halfWidth, halfHeight);
        quadrants[1] = image.getSubimage(halfWidth, 0, halfWidth, halfHeight);
        quadrants[2] = image.getSubimage(0, halfHeight, halfWidth, halfHeight);
        quadrants[3] = image.getSubimage(halfWidth, halfHeight, halfWidth, halfHeight);

        calculatedQuadrants.put(image, quadrants);

        return quadrants;
    }

}
