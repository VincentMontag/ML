package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import featureExtraction.ColorCount;
import learning.*;

public class Main {

    private static final int TRAININGS_SET_SIZE_PER_CONCEPT = 500; // whole: 1445 per concept

    public static final int DEFAULT_WIDTH = 160;

    public static final int DEFAULT_HEIGHT = 120;

    public static void main(String[] args) {
        Main main = new Main();
        main.testKNN(main);
    }

    void testKNN(Main main) {
        main.testAIModel(() -> new KNearestNeighbor(3), 20);
        // Best averag: 96%, seed: 18
        main.testAIModel(new KNearestNeighbor(3), 18);
    }

    void testCal2(Main main) {
        main.testAIModel(() -> new Cal2(), 20);
        // Best average: 64.5%, seed: 19
        main.testAIModel(new Cal2(), 19);
    }

    void createVectors(Main main) {
        // If feature vectors are not created yet
        for (Concept concept : Concept.values())
            new Thread(() -> main.createFeatureVectors(concept)).start();
    }

    void testAIModel(Supplier<Learner> learnerConstructor, int iterations) {
        double bestAverage = 0;
        int bestAverageSeed = 0;
        for (int i = 0; i < iterations; i++) {
            Learner learner = learnerConstructor.get();

            double average = testAIModel(learner, i);

            if (average > bestAverage) {
                bestAverage = average;
                bestAverageSeed = i;
            }
        }

        System.out.println("Best average: " + bestAverage + ", seed: " + bestAverageSeed);
    }

    double testAIModel(Learner learner, int seed) {
        System.out.println("Create data set with seed " + seed);
        DataSetCreator dataSetCreator = new DataSetCreator(TRAININGS_SET_SIZE_PER_CONCEPT, seed);

        List<FeatureVector> trainingsData = dataSetCreator.getTrainingsData();
        List<FeatureVector> testData = dataSetCreator.getTestData();

        System.out.println("Learn data");
        learner.learn(trainingsData);

        Map<Concept, Double> successCount = new HashMap<>();

        System.out.println("Start testing");
        for (FeatureVector testVector : testData) {
            Concept classified = learner.classify(testVector);
            if (testVector.getConcept().equals(classified))
                successCount.put(classified, 1 + successCount.getOrDefault(classified, 0.0));
        }

        System.out.println("Get results");
        double resultSum = 0;
        for (Concept c : Concept.values()) {
            double result = 100 * successCount.getOrDefault(c, 0.0) * Concept.values().length / testData.size();
            resultSum += result;
            System.out.println("AI Strength in " + c.name() + ": " + Math.round(result * 10) / 10.0 + "%");
        }
        double average = Math.round(resultSum * 10 / Concept.values().length) / 10.0;

        System.out.println();
        System.out.println("Average AI Strength: " + average + "%");
        System.out.println();

        return average;
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