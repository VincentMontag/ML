package main;

import java.util.*;
import learning.*;
import testing.DifferentSeeds;
import testing.TestAI;


public class Main {

    public static void main(String[] args) {
        // If feature vectors are not created yet
        //new CreateFeatureVectors();
        
        TestAI testWithDifferentSeeds = new DifferentSeeds();

        //testWithDifferentSeeds.testModel(() -> new KNearestNeighbor(3));
        testWithDifferentSeeds.testModel(() -> new Cal2());
    }

    public static TestResult testAIModel(Learner learner, int seed, int epoches, int traingsSetSizePerConcept) {
        System.out.println("Test AIModel [Learner: " + learner.getClass().getSimpleName() + ", seed: " + seed
                + ", epoches: " + epoches + "]");
        DataSetCreator dataSetCreator = new DataSetCreator(traingsSetSizePerConcept, seed);

        List<FeatureVector> trainingsData = dataSetCreator.getTrainingsData();
        List<FeatureVector> testData = dataSetCreator.getTestData();

        long timestamp1 = System.currentTimeMillis();
        for (int i = 0; i < epoches; i++)
            learner.learn(trainingsData);
        timestamp1 = System.currentTimeMillis() - timestamp1;

        Map<Concept, Double> successCount = new HashMap<>();

        long timestamp2 = System.currentTimeMillis();
        for (FeatureVector testVector : testData) {
            Concept classified = learner.classify(testVector);
            if (testVector.getConcept().equals(classified))
                successCount.put(classified, 1 + successCount.getOrDefault(classified, 0.0));
        }
        timestamp2 = System.currentTimeMillis() - timestamp2;

        Map<Concept, Double> successCountRecalc = new HashMap<>();
        double resultSum = 0;
        for (Concept c : Concept.values()) {
            double result = 100 * successCount.getOrDefault(c, 0.0) * Concept.values().length / testData.size();
            resultSum += result;
            result = Math.round(result * 10) / 10.0;
            successCountRecalc.put(c, result);
        }

        double average = Math.round(resultSum * 10 / Concept.values().length) / 10.0;

        return new TestResult(
                timestamp1,
                timestamp2,
                epoches,
                seed,
                trainingsData.size(),
                testData.size(),
                trainingsData.size() / Concept.values().length,
                testData.size() / Concept.values().length,
                successCountRecalc,
                average);
    }   

}