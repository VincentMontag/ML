package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import learning.*;

public class Main {

    public static void main(String[] args) {
        /*
        // If feature vectors are not created yet
        // new CreateFeatureVectors();

        Random random = new Random();
        List<Double> successRates = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            System.out.println("\nCurrent Run: " + i);
            TestResult result = testAIModel(new NeuralNetwork(27), random.nextInt(), 500, 500, false);
            System.out.println(result);
            successRates.add(result.averageSuccess());
        }

        // Standardabweichung berechnen
        double standardDeviation = calculateStandardDeviation(successRates);

        // Konfidenzintervall berechnen
        double[] confidenceInterval = calculateConfidenceInterval(successRates, 0.95);

        // Berechnung des Durchschnitts von successRates
        double totalAverageSuccess = successRates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(Double.NaN);


        // Ergs
        System.out.println("Gesamt-Durchschnitt der Erfolgsraten: " + totalAverageSuccess);
        System.out.println("Standardabweichung: " + standardDeviation);
        System.out.println("95% Konfidenzintervall: [" + confidenceInterval[0] + ", " + confidenceInterval[1] + "]");
        */

        String filePath = "src/FV_new.json";

        String[] keys = {
                "Fahrtrichtung links",
                "Fahrtrichtung rechts",
                "Stop",
                "Vorfahrt gewähren",
                "Vorfahrt von rechts",
                "Vorfahrtsstraße"
        };

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            for (String key : keys) {
                int startIndex = content.indexOf("\"" + key + "\":");
                if (startIndex == -1) {
                    System.out.println("'" + key + "' wurde nicht gefunden.");
                    continue;
                }

                int arrayStart = content.indexOf("[", startIndex);
                int arrayEnd = content.indexOf("]", arrayStart);

                if (arrayStart == -1 || arrayEnd == -1) {
                    System.out.println("Kein gültiges Array für '" + key + "' gefunden.");
                    continue;
                }

                String arrayContent = content.substring(arrayStart + 1, arrayEnd);

                String[] elements = arrayContent.split("\\},\\s*\\{");
                System.out.println("Anzahl der Elemente auf Höhe von '" + key + "': " + elements.length);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der JSON-Datei: " + e.getMessage());
        }
    }

    public static TestResult testAIModel(Learner learner, int seed, int epoches, int traingsSetSizePerConcept,
            boolean useTrainingsDataToTest) {

        System.out.println("Test AIModel [Learner: " + learner.getClass().getSimpleName() + ", seed: " + seed
                + ", epoches: " + epoches + "]");
        DataSetCreator dataSetCreator = new DataSetCreator(traingsSetSizePerConcept, seed);

        List<FeatureVector> trainingsData = dataSetCreator.getTrainingsData();
        List<FeatureVector> testData = useTrainingsDataToTest ? trainingsData : dataSetCreator.getTestData();

        long timestamp1 = System.currentTimeMillis();
        for (int i = 0; i < epoches; i++) {
            learner.learn(trainingsData);
        }
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

    // Standardabweichung
    public static double calculateStandardDeviation(List<Double> successRates) {
        double mean = successRates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        double sumSquaredDifferences = successRates.stream()
                .mapToDouble(rate -> Math.pow(rate - mean, 2))
                .sum();
        return Math.sqrt(sumSquaredDifferences / successRates.size());
    }

    // Konfidenzintervall (95%)
    public static double[] calculateConfidenceInterval(List<Double> successRates,
            double confidenceLevel) {

        double mean = successRates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double standardDeviation = calculateStandardDeviation(successRates);
        int n = successRates.size();

        // Für 95%er Konfidenzintervall (n ~ 1.96)
        double z = 1.96;
        double marginOfError = z * (standardDeviation / Math.sqrt(n));

        return new double[] { mean - marginOfError, mean + marginOfError };
    }

}