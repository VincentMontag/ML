package featureExtraction;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
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
        for (Point next : getNeighbors(current, image)) {
            if (isNeighbor(current, next, image) && this.points.get(next) != null) {
                check(current, image);
            }
        }
    }

    private Point[] getNeighbors(Point p, BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        List<Point> neighbors = new ArrayList<>();
    
        int[] dx = {0, -1, 0, 1};
        int[] dy = {-1, 0, 1, 0};
    
        for (int i = 0; i < 4; i++) {
            int newX = p.x + dx[i];
            int newY = p.y + dy[i];
    
            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                neighbors.add(new Point(newX, newY));
            }
        }
    
        return neighbors.toArray(new Point[0]);
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