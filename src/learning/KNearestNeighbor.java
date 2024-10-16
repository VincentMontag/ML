package learning;

import java.util.List;

import main.Concept;
import main.FeatureVector;

public class KNearestNeighbor implements Learner {

    private List<FeatureVector> trainingSet;

    @Override
    public void learn(List<FeatureVector> trainingSet) {
        this.trainingSet = trainingSet;
    }

    @Override
    public Concept classify(FeatureVector example) {
        FeatureVector bestMatch = null;
        double bestDistance = Double.MAX_VALUE;
        for (FeatureVector fv : this.trainingSet) {
            double distance = distance(example, fv);
            if (distance < bestDistance) {
                bestMatch = fv;
                bestDistance = distance;
            }
        }
        return bestMatch.getConcept();
    }

    private double distance(FeatureVector f1, FeatureVector f2) {
        if (f1.getNumFeatures() != f2.getNumFeatures())
            throw new RuntimeException("TypeMissmatch");
        double sum = 0;
        for (int i = 0; i < f1.getNumFeatures(); i++)
            sum += Math.pow(f1.getFeatureValue(i) - f2.getFeatureValue(i), 2);        
        return Math.sqrt(sum);
    }
    
}
