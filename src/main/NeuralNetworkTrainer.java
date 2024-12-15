package main;

import ai.djl.Device;
import ai.djl.Model;
import ai.djl.engine.Engine;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.core.Linear;
import ai.djl.nn.SequentialBlock;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.ParameterStore;
import ai.djl.training.Trainer;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.dataset.*;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.Optimizer;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class NeuralNetworkTrainer {

    public static void main(String[] args) {
        try {
            System.setProperty("ai.djl.default_engine", "MXNet");

            // Data vorbereiten
            DataPreparationResult result = parseAndSplitAndCombine(80);

            NDArray trainData = result.getTrainFeatures();
            NDArray trainLabels = result.getTrainLabels();
            NDArray testData = result.getTestFeatures();
            NDArray testLabels = result.getTestLabels();

            System.out.println("Gesamt-TrainingsNDArray erstellt mit Größe: " + trainData.size());
            System.out.println("Gesamt-TestNDArray erstellt mit Größe: " + testData.size());

            trainAndEvaluate(trainData, trainLabels, testData, testLabels);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataPreparationResult parseAndSplitAndCombine(int trainPercentage) throws IOException {
        DataPreparation handler = new DataPreparation();
        DataPreparationResult result = handler.parseAndSplit(trainPercentage);
        return result;
    }

    public static void trainAndEvaluate(NDArray trainData, NDArray trainLabels, NDArray testData, NDArray testLabels){
        try(NDManager manager = NDManager.newBaseManager()) {

            // Modell erstellen
            Model model = createModel(manager);

            // Konfig und Training
            DefaultTrainingConfig config = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                    .optDevices(Engine.getInstance().getDevices(1))
                    .addTrainingListeners(new TrainingListener() {

                        @Override
                        public void onTrainingBegin(Trainer trainer) {
                            System.out.println("Training gestartet...");
                        }

                        @Override
                        public void onEpoch(Trainer trainer) {
                            System.out.println("Epoche abgeschlossen.");
                        }

                        @Override
                        public void onTrainingBatch(Trainer trainer, BatchData batchData) {
                            Map<Device, NDList> labelsMap = batchData.getLabels();

                            if (labelsMap != null && !labelsMap.isEmpty()) {
                                NDList labelsList = labelsMap.values().iterator().next();
                                if (labelsList != null && !labelsList.isEmpty()) {
                                    NDArray labels = labelsList.get(0).toType(DataType.INT32, true);
                                    int[] intLabels = labels.toIntArray();
                                    //System.out.println("Labels Batch: " + Arrays.toString(intLabels));
                                } else {
                                    System.out.println("Keine gültigen NDList-Daten gefunden.");
                                }
                            } else {
                                System.out.println("Keine Labels-Daten gefunden.");
                            }
                        }


                        @Override
                        public void onValidationBatch(Trainer trainer, BatchData batchData) {
                            System.out.println("Validierungsdaten Batch verarbeitet...");
                        }

                        @Override
                        public void onTrainingEnd(Trainer trainer) {
                            System.out.println("Training abgeschlossen.");
                        }
                    });

            Trainer trainer = model.newTrainer(config);

            System.out.println("Train-Dimension: " + trainData.getShape());
            System.out.println("Train-Labels-Dimension: " + trainLabels.getShape());

            // Training initialisieren
            trainer.initialize(new Shape(trainData.getShape().get(1)));

            // Dataset erstellen
            Dataset trainingDataset = new ArrayDataset.Builder()
                    .setData(trainData)
                    .optLabels(trainLabels)
                    .setSampling(32, true)
                    .build();


            // Trainingsschleife über mehrere Epochen
            int numOfEpochs = 10;
            EasyTrain.fit(trainer, numOfEpochs, trainingDataset, null);

            // Evaluation
            evaluateModel(model, testData, testLabels);

            trainer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    // NN Layout mit Neuronen pro Layer und ReLu
    public static Model createModel(NDManager manager) {
        Model model = Model.newInstance("feedforward-network");

        SequentialBlock block = new SequentialBlock();
        block.add(Linear.builder().setUnits(500).build())
                .add(Activation.reluBlock())
                .add(Linear.builder().setUnits(200).build())
                .add(Activation.reluBlock())
                .add(Linear.builder().setUnits(50).build())
                .add(Activation.reluBlock())
                .add(Linear.builder().setUnits(6).build());

        model.setBlock(block);

        return model;
    }

    public static void evaluateModel(Model model, NDArray testData, NDArray testLabels) {
        try (NDManager manager = NDManager.newBaseManager()) {
            ParameterStore parameterStore = new ParameterStore(manager, false);

            // Vorhersagen erhalten
            NDList inputList = new NDList(testData);
            NDList predictions = model.getBlock().forward(parameterStore, inputList, true);

            // Labels und Vorhersagen extrahieren
            NDArray predictedArray = predictions.get(0).argMax(1).toType(DataType.INT32, false);
            NDArray actualLabels = testLabels.toType(DataType.INT32, false);

            int numClasses = 6;
            int[][] confusionMatrix = new int[numClasses][numClasses];

            // Confusion-Matrix befüllen
            for (int i = 0; i < actualLabels.size(); i++) {
                int trueLabel = actualLabels.getInt(i);
                int predictedLabel = predictedArray.getInt(i);

                confusionMatrix[trueLabel][predictedLabel]++;
            }

            // Confusion Matrix ausgeben
            System.out.println("Confusion Matrix:");
            for (int i = 0; i < numClasses; i++) {
                System.out.println(Arrays.toString(confusionMatrix[i]));
            }

            // Klassenweise Genauigkeit berechnen
            System.out.println("Klassenweise Genauigkeit:");
            for (int i = 0; i < numClasses; i++) {
                int truePositives = confusionMatrix[i][i];
                int totalActual = Arrays.stream(confusionMatrix[i]).sum();
                double classAccuracy = (double) truePositives / totalActual;

                System.out.printf("Klasse %d: %.2f%%%n", i, classAccuracy * 100);
            }
        }
    }
}
