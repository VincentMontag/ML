package main;

import java.awt.image.BufferedImage;
import java.util.List;

public class FeatureVectorGenerator implements FeatureVector {

    private final Concept concept;
    private final int[] features;

    FeatureVectorGenerator(BufferedImage image, int clusteringCount, Concept concept, List<FeatureExtractor> featureExtractors) {
        BufferedImage[] subImages = new Clustering().cluster(image, clusteringCount);
        this.concept = concept;
        this.features = new int[featureExtractors.size() * subImages.length];
        for (int i = 0; i < featureExtractors.size(); i++)
            for (int k = 0; k < subImages.length; k++)
                this.features[i * subImages.length + k] = featureExtractors.get(i).extractFeature(subImages[k]);
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
    
}