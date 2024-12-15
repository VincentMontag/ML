package main;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataPreparation {

    private static final String jsonFilePath = "src/FV_new.json";
    private static final List<String> CONCEPTS = Arrays.asList(
            "Fahrtrichtung links",
            "Fahrtrichtung rechts",
            "Stop",
            "Vorfahrt gewahren",
            "Vorfahrt von rechts",
            "Vorfahrtsstrasse"
    );

    public DataPreparationResult parseAndSplit(int trainPercentage) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(new File(jsonFilePath));

        Map<String, List<List<String>>> conceptData = new HashMap<>();
        Map<String, Integer> conceptCounters = new HashMap<>();

        while (parser.nextToken() != JsonToken.START_OBJECT) {}

        while (parser.nextToken() != null) {
            if (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
                String currentKey = parser.getCurrentName();
                if (CONCEPTS.contains(currentKey)) {
                    parser.nextToken();
                    if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
                        List<List<String>> images = new ArrayList<>();

                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                                while (parser.nextToken() != JsonToken.END_OBJECT) {
                                    if ("colors".equals(parser.getCurrentName())) {
                                        parser.nextToken();
                                        if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
                                            List<String> colors = new ArrayList<>();
                                            while (parser.nextToken() != JsonToken.END_ARRAY) {
                                                colors.add(parser.getText());
                                            }
                                            images.add(colors);
                                        }
                                    }
                                }
                            }
                        }

                        conceptData.put(currentKey, images);
                        conceptCounters.put(currentKey, images.size());
                    }
                }
            }
        }

        NDManager manager = NDManager.newBaseManager();
        List<NDArray> trainFeaturesList = new ArrayList<>();
        List<NDArray> trainLabelsList = new ArrayList<>();
        List<NDArray> testFeaturesList = new ArrayList<>();
        List<NDArray> testLabelsList = new ArrayList<>();

        int totalImagesPerConcept = 0;

        for (String concept : CONCEPTS) {
            List<List<String>> images = conceptData.getOrDefault(concept, new ArrayList<>());
            Collections.shuffle(images, new Random());

            System.out.println("Konzept: \"" + concept + "\" enthält " + images.size() + " Bilder.");

            int trainCount = Math.min(Math.round(images.size() * (trainPercentage / 100.0f)), 7225);
            List<List<String>> trainData = images.subList(0, trainCount);
            List<List<String>> testData = images.subList(trainCount, Math.min(images.size(), 7225));

            System.out.println("Training-Daten-Anzahl für Konzept '" + concept + "': " + trainData.size());
            System.out.println("Test-Daten-Anzahl für Konzept '" + concept + "': " + testData.size());

            NDArray trainFeatures = createNDArray(manager, trainData);
            NDArray trainLabels = createLabels(manager, trainData.size(), CONCEPTS.indexOf(concept));
            NDArray testFeatures = createNDArray(manager, testData);
            NDArray testLabels = createLabels(manager, testData.size(), CONCEPTS.indexOf(concept));

            // Füge NDArray zu den Listen hinzu
            trainFeaturesList.add(trainFeatures);
            trainLabelsList.add(trainLabels);
            testFeaturesList.add(testFeatures);
            testLabelsList.add(testLabels);

            System.out.println("Train NDArray-Größe für Konzept '" + concept + "': " + trainFeatures.size());
            System.out.println("Test NDArray-Größe für Konzept '" + concept + "': " + testFeatures.size());
            System.out.println();
        }

        System.out.println("Gesamtzahl der Trainingsdaten aus allen Klassen: " + totalImagesPerConcept);

        NDArray totalTrainFeatures = combineNDArray(manager, trainFeaturesList);
        NDArray totalTrainLabels = combineNDArray(manager, trainLabelsList);
        NDArray totalTestFeatures = combineNDArray(manager, testFeaturesList);
        NDArray totalTestLabels = combineNDArray(manager, testLabelsList);

        return new DataPreparationResult(totalTrainFeatures, totalTrainLabels, totalTestFeatures, totalTestLabels); // NEU: Testlabels zurückgeben
    }


    private NDArray createNDArray(NDManager manager, List<List<String>> data) {
        float[][] arrayData = new float[data.size()][data.isEmpty() ? 0 : data.get(0).size() * 3];

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(0).size(); j++) {
                try {
                    // Extrahiere RGB-Werte aus dem String und teile sie korrekt auf
                    String rgbString = data.get(i).get(j); // (255,255,255)
                    String[] splitRGB = rgbString.replaceAll("[()]", "").split(","); // Entferne Klammern und splitte
                    float r = Float.parseFloat(splitRGB[0]) / 255.0f; // Normalisiere auf 0-1
                    float g = Float.parseFloat(splitRGB[1]) / 255.0f;
                    float b = Float.parseFloat(splitRGB[2]) / 255.0f;

                    arrayData[i][j * 3] = r;
                    arrayData[i][j * 3 + 1] = g;
                    arrayData[i][j * 3 + 2] = b;
                } catch (Exception e) {
                    System.err.println("Fehler beim Parsing RGB-Wert: " + data.get(i).get(j));
                }
            }
        }

        NDArray result = manager.create(arrayData);

       return result;
    }



    private NDArray createLabels(NDManager manager, int size, int labelIndex) {
        float[] labels = new float[size];
        Arrays.fill(labels, labelIndex);
        NDArray labelND = manager.create(labels);

        return labelND;
    }

    private NDArray combineNDArray(NDManager manager, List<NDArray> arrays) {
        if (arrays.isEmpty()) {
            System.err.println("Keine NDArray-Daten vorhanden zum Kombinieren.");
            return manager.create(new float[0]);
        }

        NDArray result = null; // Start leer

        for (int i = 0; i < arrays.size(); i++) {
            NDArray currentArray = arrays.get(i);

            // Logging vor dem Kombinieren
            System.out.println("NDArray Index " + i + ": Shape = " + Arrays.toString(currentArray.getShape().getShape()));

            if (currentArray == null) {
                System.err.println("Array ist null bei Index: " + i);
                continue; // Überspringe leere Arrays
            }

            if (result == null) {
                result = currentArray; // Setze das erste valide Array
            } else {
                try {
                    result = result.concat(currentArray, 0); // Versuche zusammenzuführen
                } catch (Exception e) {
                    System.err.println("Fehler beim Kombinieren von Arrays bei Index: " + i);
                    e.printStackTrace();
                }
            }
        }

        if (result == null) {
            System.err.println("Kein NDArray konnte kombiniert werden.");
            return manager.create(new float[0]);
        }

        // Logging der endgültigen kombinierten Form
        System.out.println("Endgültige kombinierte NDArray-Shape: " + Arrays.toString(result.getShape().getShape()));
        return result;
    }
}
