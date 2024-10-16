package main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Util {

    public static Color getColorOfPixel(int argb) {
        int alpha = (argb >> 24) & 0xff;
        int red = (argb >> 16) & 0xff;
        int green = (argb >> 8) & 0xff;
        int blue = (argb) & 0xff;
        float luminance = (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;
        return luminance >= 0.5f ? getBright(red, green, blue) : getDark(red, green, blue);
    }

    private static Color getBright(int r, int g, int b) {
        if (Math.abs(r - g) < 10 && Math.abs(g - b) < 10 && Math.abs(r - b) < 10 && r > 245)
            return Color.UNKNOWN;
        if ((r > g + 20 && r > b + 20) && (Math.abs(g - b) < 40))
            return Color.RED;
        else if ((r >= g && r > b) && (g - b > 40))
            return Color.YELLOW;
        else if (Math.abs(r - g) < 30 && Math.abs(g - b) < 30 && Math.abs(r - b) < 30)
            return Color.BLACK;
        else if (r < g && g < b && r > 170)
            return Color.BLUE;
        return Color.UNKNOWN;
    }

    private static Color getDark(int r, int g, int b) {
        if ((r > g + 20 && r > b + 20) && (Math.abs(g - b) < 40))
            return Color.RED;
        else if ((r >= g && r > b) && (g - b > 40))
            return Color.YELLOW;
        else if (Math.abs(r - g) < 15 && Math.abs(g - b) < 15 && Math.abs(r - b) < 15 && r < 30)
            return Color.BLACK;
        else if (r < g && g < b && r < 75)
            return Color.BLUE;
        return Color.UNKNOWN;
    }

    public static BufferedImage scale(BufferedImage image, int width, int height) {
        if (image.getWidth() == Main.DEFAULT_WIDTH && image.getHeight() == Main.DEFAULT_HEIGHT)
            return image;
        BufferedImage scaledImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();
        return scaledImage;
    }

    public static BufferedImage crop(BufferedImage image) {
        int left = 0, top = 0, right = 0, bottom = 0;

        // Left
        out: for (int i = 0; i < image.getWidth(); i++) {
            for (int k = 0; k < image.getHeight(); k++) {
                int rgb = image.getRGB(i, k);
                Color color = Util.getColorOfPixel(rgb);
                if (!color.equals(Color.UNKNOWN)) {
                    left = i;
                    break out;
                }
            }
        }

        // Top
        out: for (int i = 0; i < image.getHeight(); i++) {
            for (int k = 0; k < image.getWidth(); k++) {
                int rgb = image.getRGB(k, i);
                Color color = Util.getColorOfPixel(rgb);
                if (!color.equals(Color.UNKNOWN)) {
                    top = i;
                    break out;
                }
            }
        }

        // Right
        out: for (int i = image.getWidth() - 1; i > 0; i--) {
            for (int k = image.getHeight() - 1; k > 0; k--) {
                int rgb = image.getRGB(i, k);
                Color color = Util.getColorOfPixel(rgb);
                if (!color.equals(Color.UNKNOWN)) {
                    right = i;
                    break out;
                }
            }
        }

        // Bottom
        out: for (int i = image.getHeight() - 1; i > 0; i--) {
            for (int k = 0; k < image.getWidth(); k++) {
                int rgb = image.getRGB(k, i);
                Color color = Util.getColorOfPixel(rgb);
                if (!color.equals(Color.UNKNOWN)) {
                    bottom = i;
                    break out;
                }
            }
        }

        return image.getSubimage(left, top, right - left, bottom - top);
    }

}