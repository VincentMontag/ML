package main;

import ai.djl.ndarray.NDArray;

import java.util.List;

public class DataPreparationResult {

    private final List<NDArray> trainNDArrays;
    private final List<NDArray> testNDArrays;

    public DataPreparationResult(List<NDArray> trainNDArrays, List<NDArray> testNDArrays) {
        this.trainNDArrays = trainNDArrays;
        this.testNDArrays = testNDArrays;
    }

    public NDArray getTotalTrainingNDArray() {
        return concatenateArrays(trainNDArrays);
    }

    public NDArray getTotalTestNDArray() {
        return concatenateArrays(testNDArrays);
    }

    private NDArray concatenateArrays(List<NDArray> ndArrays) {
        if (ndArrays.isEmpty()) return null;

        NDArray combined = ndArrays.get(0);
        for (int i = 1; i < ndArrays.size(); i++) {
            combined = combined.concat(ndArrays.get(i), 0);
        }
        return combined;
    }
}
