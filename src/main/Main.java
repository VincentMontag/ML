package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import featureExtraction.ColorCount;
import learning.*;

public class Main {

    private static final int ITERATIONS = 1;

    private static final int TRAININGS_SET_SIZE_PER_CONCEPT = 10;

    private static final int K_NEAREST_NEIGHBOR = 5;
    
    public static final int DEFAULT_WIDTH = 160;

    public static final int DEFAULT_HEIGHT = 120;

    public static void main(String[] args) {
        Main main = new Main();

        // If feature vectors are not created yet
        //for (Concept concept : Concept.values())
        //    main.createFeatureVectors(concept);

        for (int i = 0; i < ITERATIONS; i++) {
            int seed = (int) (Math.random() * ITERATIONS);
            System.out.println("Create data set with random seed " + seed);
            DataSetCreator dataSetCreator = new DataSetCreator(TRAININGS_SET_SIZE_PER_CONCEPT, seed);
            
            Set<FeatureVector> trainingsData = dataSetCreator.getTrainingsData();
            Set<FeatureVector> testData = dataSetCreator.getTestData();
            
            Learner learner = new KNearestNeighbor(K_NEAREST_NEIGHBOR);
            learner.learn(trainingsData);

            int successCount = 0;

            for (FeatureVector testVector : testData) {
                Concept classified = learner.classify(testVector);
                if (classified.equals(testVector.getConcept()))
                    successCount++;
            }

            System.out.println("AI Strength: " + (double) successCount / testData.size());            
        }
        
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
    }

    void printFeatureVector(FeatureVector vector) {
        int[] values = new int[vector.getNumFeatures()];
        for (int i = 0; i < values.length; i++)
            values[i] = vector.getFeatureValue(i);
        System.out.println(Arrays.toString(values));
    }

    void createFeatureVectors(Concept concept) {
        for (int i = -DataSetCreator.RANGE; i <= DataSetCreator.RANGE; i += 10) {
            for (int k = -DataSetCreator.RANGE; k <= DataSetCreator.RANGE; k += 10) {
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