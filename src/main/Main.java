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

    private static final int TRAININGS_SET_SIZE_PER_CONCEPT = 500; // whole: 1445 per concept

    public static final int DEFAULT_WIDTH = 160;

    public static final int DEFAULT_HEIGHT = 120;

    public static void main(String[] args) {
        Main main = new Main();

        // If feature vectors are not created yet
        //for (Concept concept : Concept.values()) new Thread(() -> main.createFeatureVectors(concept)).start();

        // Test model
        //main.testAIModel(new KNearestNeighbor(5));
        main.testAIModel(new Cal2());
    }

    void testAIModel(Learner learner) {
        for (int i = 0; i < ITERATIONS; i++) {
            int seed = (int) (Math.random() * ITERATIONS);
            System.out.println("Create data set with random seed " + seed);
            DataSetCreator dataSetCreator = new DataSetCreator(TRAININGS_SET_SIZE_PER_CONCEPT, seed);

            List<FeatureVector> trainingsData = dataSetCreator.getTrainingsData();
            List<FeatureVector> testData = dataSetCreator.getTestData();

            System.out.println("Learn data");
            learner.learn(trainingsData);

            Map<Concept, Double> successCount = new HashMap<>();

            System.out.println("Start testing");
            for (FeatureVector testVector : testData) {
                Concept classified = learner.classify(testVector);
                if (classified.equals(testVector.getConcept()))
                    successCount.put(classified, 1 + successCount.getOrDefault(classified, 0.0));
            }

            System.out.println("Get results");
            double resultSum = 0;
            for (Concept c : Concept.values()) {
                double result = 100 * successCount.getOrDefault(c, 0.0) * Concept.values().length / testData.size();
                resultSum += result;
                System.out.println("AI Strength in " + c.name() + ": " + Math.round(result * 10) / 10.0 + "%");
            }

            System.out.println();
            System.out.println("Average AI Strength: " + Math.round(resultSum * 10 / Concept.values().length) / 10.0 + "%");
            System.out.println();
        }
    }

    FeatureVector createFeatureVector(BufferedImage image, Concept concept) {
        // Generates clusteringCount times the number of feature extractors vectors
        return new FeatureVectorGenerator(image, 9, concept, Arrays.asList(
                new ColorCount(Color.BLUE),
                new ColorCount(Color.RED),
                new ColorCount(Color.YELLOW)));
    }

    void printFeatureVector(FeatureVector vector) {
        int[] values = new int[vector.getNumFeatures()];
        for (int i = 0; i < values.length; i++)
            values[i] = vector.getFeatureValue(i);
        System.out.println(Arrays.toString(values));
    }

    void createFeatureVectors(Concept concept) {
        System.out.println("Create vectors for " + concept.name());
        for (int i = -DataSetCreator.RANGE; i <= DataSetCreator.RANGE; i += 10) {
            for (int k = -DataSetCreator.RANGE; k <= DataSetCreator.RANGE; k += 10) {
                for (int b = -2; b <= 2; b++) {
                    String bStr = Util.getBrightness(b);
                    String xy = "X" + i + "Y" + k;
                    FeatureVector vector = createFeatureVector(
                            getImage("Verkehrszeichen/" + concept.name() + "/" + bStr + "/80x60/" + xy + ".bmp"),
                            concept);
                    FileManager.writeFile(vector, concept.name() + bStr + xy);
                }
            }
        }
    }

    BufferedImage getImage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            BufferedImage scaled = Util.scale(image, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            BufferedImage cropped = Util.crop(scaled);
            return cropped;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}