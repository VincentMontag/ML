package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import featureExtraction.ColorCount;
import featureExtraction.CornerCount;
import gui.ImageDisplay;
import learning.*;

public class Main {

    private static final int RANGE = 30; // FeatureVector count per concept: (RANGE * 0.2 + 1)^2

    public static final int DEFAULT_WIDTH = 160;

    public static final int DEFAULT_HEIGHT = 120;

    public static void main(String[] args) {
        Main main = new Main();

        List<FeatureVector> fvs = main.readAllFeatureVectors();
        Learner learner = new KNearestNeighbor();

        learner.learn(fvs);

        FeatureVector test = main.createFeatureVector(
                main.getImage("Verkehrszeichen/Vorfahrtsstraße/0/80x60/X-40Y-40.bmp"),
                Concept.Vorfahrtsstraße);

        Concept result = learner.classify(test);
        System.out.println(result);
        
        // for (Concept concept : Concept.values())
        // main.createFeatureVectors(concept);

        // main.printFeatureVector(FileManager.readFile("StopX0Y0"));
        // main.rotationExample();
        // FeatureVector test =
        // main.createFeatureVector(main.getImage("Verkehrszeichen/Stop/0/80x60/X-10Y-10.bmp"),
        // Concept.Stop);
        // main.printFeatureVector(test);

        // main.getImage("Verkehrszeichen/Stop/0/3500/X0Y0.jpg");
    }

    List<FeatureVector> readAllFeatureVectors() {
        List<FeatureVector> fvs = new ArrayList<>();
        for (Concept concept : Concept.values()) {
            for (int i = -RANGE; i <= RANGE; i += 10) {
                for (int k = -RANGE; k <= RANGE; k += 10) {
                    String xy = "X" + i + "Y" + k;
                    FeatureVector fv = FileManager.readFile(concept.name() + xy);
                    fvs.add(fv);
                }
            }
        }
        return fvs;
    }

    FeatureVector createFeatureVector(BufferedImage image, Concept concept) {
        System.out.println("Create FeatureVector for " + concept.name());
        return new FeatureVectorGenerator(image, concept, Arrays.asList(
                new ColorCount(0, Color.BLUE),
                new ColorCount(1, Color.BLUE),
                new ColorCount(2, Color.BLUE),
                new ColorCount(3, Color.BLUE),

                new ColorCount(0, Color.RED),
                new ColorCount(1, Color.RED),
                new ColorCount(2, Color.RED),
                new ColorCount(3, Color.RED),

                new ColorCount(0, Color.YELLOW),
                new ColorCount(1, Color.YELLOW),
                new ColorCount(2, Color.YELLOW),
                new ColorCount(3, Color.YELLOW)));

        // new CornerCount()));
    }

    void printFeatureVector(FeatureVector vector) {
        int[] values = new int[vector.getNumFeatures()];
        for (int i = 0; i < values.length; i++)
            values[i] = vector.getFeatureValue(i);
        System.out.println(Arrays.toString(values));
    }

    void createFeatureVectors(Concept concept) {
        for (int i = -RANGE; i <= RANGE; i += 10) {
            for (int k = -RANGE; k <= RANGE; k += 10) {
                String xy = "X" + i + "Y" + k;
                FeatureVector vector = createFeatureVector(
                        getImage("Verkehrszeichen/" + concept.name() + "/0/80x60/" + xy + ".bmp"), concept);
                FileManager.writeFile(vector, concept.name() + xy);
            }
        }
    }

    BufferedImage getImage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            BufferedImage scaled = Util.scale(image, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            System.out.println(scaled.getWidth() + " " + scaled.getHeight());
            BufferedImage cropped = Util.crop(scaled);
            // new ImageDisplay(cropped);
            return cropped;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}