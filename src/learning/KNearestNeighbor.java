package learning;

import main.Concept;
import main.FeatureExtractor;
import main.FeatureVector;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNearestNeighbor implements FeatureVector {

    private Concept concept;
    private int[] features;

    KNearestNeighbor(BufferedImage image, Concept concept, List<FeatureExtractor> featureExtractors) {
        this.concept = concept;
        this.features = new int[featureExtractors.size()];
        for (int i = 0; i < featureExtractors.size(); i++)
            features[i] = featureExtractors.get(i).extractFeature(image);
    }

    public KNearestNeighbor() {
    }

    @Override
    public Concept getConcept() {
        return this.concept;
    }

    @Override
    public int getNumFeatures() {
        return this.features.length;
    }

    @Override
    public int getFeatureValue(int i) {
        return this.features[i];
    }

    // Calculate euclidian distance
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

    public Concept classify(int k, FeatureVector inputFeatureVector, List<FeatureVector> allFeatureVectors) {
        // Safe neighbors in list
        List<Neighbor> neighbors = new ArrayList<>();

        // Calculate distance and save them
        for (FeatureVector featureVector : allFeatureVectors) {
            double distance = calculateEuclideanDistance(inputFeatureVector, featureVector);
            neighbors.add(new Neighbor(featureVector, distance));
        }

        neighbors.sort(Comparator.comparingDouble(Neighbor::getDistance));
        List<Neighbor> kNearestNeighbors = neighbors.subList(0, k);

        // count number of concepts
        Map<Concept, Integer> conceptCount = new HashMap<>();
        for (Neighbor neighbor : kNearestNeighbors) {
            Concept concept = neighbor.getFeatureVector().getConcept();
            conceptCount.put(concept, conceptCount.getOrDefault(concept, 0) + 1);
        }

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