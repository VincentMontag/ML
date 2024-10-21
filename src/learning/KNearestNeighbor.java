package learning;

import main.Concept;
import main.FeatureExtractor;
import main.FeatureVector;
import learning.Learner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNearestNeighbor implements Learner {
    // Store the training set
    private List<FeatureVector> trainingSet;
    // Number of neighbors
    private int k;

    public KNearestNeighbor(int k) {
        this.k = k;
        this.trainingSet = new ArrayList<>();
    }

    public KNearestNeighbor(BufferedImage image, Concept concept, List<FeatureExtractor> featureExtractors) {
        this.k = 3;
        this.trainingSet = new ArrayList<>();
        int[] features = new int[featureExtractors.size()];
        for (int i = 0; i < featureExtractors.size(); i++) {
            features[i] = featureExtractors.get(i).extractFeature(image);
        }
    }

    // Learning method to store the training set
    @Override
    public void learn(List<FeatureVector> trainingSet) {
        this.trainingSet = trainingSet; // Simply store the training data
    }

    // Classify a given feature vector based on the training data
    @Override
    public Concept classify(FeatureVector example) {
        if (trainingSet.isEmpty()) {
            throw new IllegalStateException("Training set is empty. Learn before classifying.");
        }

        List<Neighbor> neighbors = new ArrayList<>();
        for (FeatureVector featureVector : trainingSet) {
            double distance = calculateEuclideanDistance(example, featureVector);
            neighbors.add(new Neighbor(featureVector, distance));
        }

        neighbors.sort(Comparator.comparingDouble(Neighbor::getDistance));
        List<Neighbor> kNearestNeighbors = neighbors.subList(0, Math.min(k, neighbors.size()));

        // Count the concepts among k nearest neighbors
        Map<Concept, Integer> conceptCount = new HashMap<>();
        for (Neighbor neighbor : kNearestNeighbors) {
            Concept concept = neighbor.getFeatureVector().getConcept();
            conceptCount.put(concept, conceptCount.getOrDefault(concept, 0) + 1);
        }

        // Determine the most common concept
        Concept mostCommonConcept = null;
        int maxCount = 0;
        for (Map.Entry<Concept, Integer> entry : conceptCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostCommonConcept = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return mostCommonConcept;
    }

    // Calculate Euclidean distance between two feature vectors
    private static double calculateEuclideanDistance(FeatureVector a, FeatureVector b) {
        if (a.getNumFeatures() != b.getNumFeatures()) {
            throw new IllegalArgumentException("Feature vectors must have the same number of features");
        }
        double sum = 0;
        for (int i = 0; i < a.getNumFeatures(); i++) {
            int diff = a.getFeatureValue(i) - b.getFeatureValue(i);
            sum += diff * diff;
        }

        return Math.sqrt(sum);
    }

    // Helper class to store a FeatureVector and its distance
    private static class Neighbor {
        private final FeatureVector featureVector;
        private final double distance;

        public Neighbor(FeatureVector featureVector, double distance) {
            this.featureVector = featureVector;
            this.distance = distance;
        }

        public FeatureVector getFeatureVector() {
            return featureVector;
        }

        public double getDistance() {
            return distance;
        }
    }
}
