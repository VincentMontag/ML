package main;

import java.awt.image.BufferedImage;
import java.util.List;

public class FeatureVectorGenerator implements FeatureVector {

    private final Concept concept;
    private final int[] features;

    FeatureVectorGenerator(BufferedImage image, Concept concept, List<FeatureExtractor> featureExtractors) {
        this.concept = concept;
        this.features = new int[featureExtractors.size()];
        for (int i = 0; i < featureExtractors.size(); i++)
            features[i] = featureExtractors.get(i).extractFeature(image);
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