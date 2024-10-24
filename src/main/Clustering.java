package main;

import java.awt.image.BufferedImage;

public class Clustering {

    BufferedImage[] cluster(BufferedImage image, int clusteringCount) {
        int ccBase = (int) Math.sqrt(clusteringCount);
        
        int width = image.getWidth() / ccBase;
        int height = image.getHeight() / ccBase;

        BufferedImage[] quadrants = new BufferedImage[clusteringCount];
        for (int i = 0; i < ccBase; i++)
            for (int k = 0; k < ccBase; k++)
                quadrants[i * ccBase + k] = image.getSubimage(i * width, k * height, width, height);

        return quadrants;
    }
    
}
