package featureExtraction;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.Color;
import main.FeatureExtractor;
import main.Point;
import main.Util;

public class ConnectionsCount implements FeatureExtractor {

    Map<Point, Boolean> points = new HashMap<>();

    @Override
    public int extractFeature(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                this.points.put(new Point(x, y), true);
            }
        }
        int count = 0;
        while (!this.points.isEmpty()) {
            check(this.points.keySet().iterator().next(), image);
            count++;
        }
        return count;
    }

    void check(Point current, BufferedImage image) {
        this.points.remove(current);
        for (Point next : getNeighbors(current)) {
            if (isNeighbor(current, next, image) && this.points.get(next) != null) {
                check(current, image);
            }
        }
    }

    private Point[] getNeighbors(Point p, BufferedImage image) {
        if (p.x == 0) {
            return new Point[] { new Point(p.x + 1, p.y), new Point(p.x, p.y + 1)};
        } else if (p.x == 0 && p.y == 0) {
            return new Point[] { new Point(p.x + 1, p.y), new Point(p.x, p.y + 1)};
        }
    }

    private boolean isNeighbor(Point p, Point neighbor, BufferedImage image) {
        int rgb1 = image.getRGB(p.x, p.y);
        Color color1 = Util.getColorOfPixel(rgb1);
        int rgb2 = image.getRGB(p.x, p.y);
        Color color2 = Util.getColorOfPixel(rgb2);
        if (color1.equals(color2))
            return true;
        return false;
    }
    
}