package main;

import ai.djl.ndarray.NDArray;

public class DataPreparationResult {
    private NDArray trainFeatures;
    private NDArray trainLabels;
    private NDArray testFeatures;
    private NDArray testLabels;

    public DataPreparationResult(NDArray trainFeatures, NDArray trainLabels, NDArray testFeatures, NDArray testLabels) {
        this.trainFeatures = trainFeatures;
        this.trainLabels = trainLabels;
        this.testFeatures = testFeatures;
        this.testLabels = testLabels;
    }

    public NDArray getTrainFeatures() {
        return trainFeatures;
    }

    public NDArray getTrainLabels() {
        return trainLabels;
    }

    public NDArray getTestFeatures() {
        return testFeatures;
    }

    public NDArray getTestLabels() { // NEU
        return testLabels;
    }
}

