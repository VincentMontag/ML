package learning;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.Concept;
import main.FeatureVector;

public class Cal2 implements Learner {

    private final Tree tree;

    public Cal2() {
        this.tree = new Tree(0);
    }

    @Override
    public void learn(List<FeatureVector> trainingSet) {
        Collections.shuffle(trainingSet, new Random(0));
        for (FeatureVector fv : trainingSet) {
            Tree leaf = classify(this.tree, fv);
            // Current trainings vector is already classified correctly
            if (fv.getConcept().equals(leaf.concept))
                continue;

            // Concept is unknown
            if (leaf.concept == null) {
                leaf.concept = fv.getConcept();
                continue;
            }

            if (leaf.index < fv.getNumFeatures() - 1) {
                // Expand tree
                Tree newLeaf = new Tree(leaf.index + 1);
                newLeaf.concept = fv.getConcept();
                leaf.map.put(fv.getFeatureValue(leaf.index), newLeaf);
            } else {
                // Update leaf concept
                leaf.concept = fv.getConcept();
            }
        }
    }

    @Override
    public Concept classify(FeatureVector example) {
        return classify(this.tree, example).concept;
    }

    private Tree classify(Tree tree, FeatureVector vector) {
        Tree next = tree.map.get(vector.getFeatureValue(tree.index));
        if (next == null)
            return tree;
        return classify(next, vector);
    }

}

class Tree {

    final int index;

    final Map<Integer, Tree> map;

    Concept concept;

    Tree(int index) {
        this.index = index;
        this.map = new HashMap<>();
    }

}