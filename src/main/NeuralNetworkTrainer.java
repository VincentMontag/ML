package main;

import ai.djl.ndarray.NDArray;
import java.io.IOException;

public class NeuralNetworkTrainer {

    public static void main(String[] args) {
        try {
            DataPreparationResult result = parseAndSplitAndCombine(80);

            NDArray totalTrainingNDArray = result.getTotalTrainingNDArray();
            NDArray totalTestNDArray = result.getTotalTestNDArray();

            System.out.println("Gesamt-TrainingsNDArray erstellt mit Größe: " + totalTrainingNDArray.size());
            System.out.println("Gesamt-TestNDArray erstellt mit Größe: " + totalTestNDArray.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataPreparationResult parseAndSplitAndCombine(int trainPercentage) throws IOException {
        DataPreparation handler = new DataPreparation();
        DataPreparationResult result = handler.parseAndSplit(trainPercentage);

        return result;
    }
}
