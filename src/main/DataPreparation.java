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
        List<NDArray> trainNDArrays = new ArrayList<>();
        List<NDArray> testNDArrays = new ArrayList<>();

        for (String concept : CONCEPTS) {
            List<List<String>> images = conceptData.getOrDefault(concept, new ArrayList<>());
            // Random shufflen
            Collections.shuffle(images, new Random());

            int trainCount = Math.round(images.size() * (trainPercentage / 100.0f));
            List<List<String>> trainData = images.subList(0, trainCount);
            List<List<String>> testData = images.subList(trainCount, images.size());

            NDArray trainNDArray = createNDArray(manager, trainData);
            NDArray testNDArray = createNDArray(manager, testData);

            trainNDArrays.add(trainNDArray);
            testNDArrays.add(testNDArray);

            // Logging-Ausgabe
            System.out.println("Konzept: " + concept + " NDArrays werden erstellt und random geshuffled...");
            System.out.println("Trainings NDArray: " + trainNDArray.size() + " (" + trainNDArray.size() / 400 + " Bilder)");
            System.out.println("Test NDArray: " + testNDArray.size() + " (" + testNDArray.size() / 400 + " Bilder)");
            System.out.println("Bilder in " + concept + ": " + conceptCounters.getOrDefault(concept, 0) + "\n");
        }

        return new DataPreparationResult(trainNDArrays, testNDArrays);
    }

    private NDArray createNDArray(NDManager manager, List<List<String>> data) {
        float[][] arrayData = new float[data.size()][data.isEmpty() ? 0 : data.get(0).size()];

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(0).size(); j++) {
                try {
                    arrayData[i][j] = Float.parseFloat(data.get(i).get(j));
                } catch (NumberFormatException e) {
                    arrayData[i][j] = 0.0f;
                }
            }
        }

        return manager.create(arrayData);
    }
}
