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

    public static void main(String[] args) {
        try {
            parseAndSplit(80); // 80% Training, 20% Test
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseAndSplit(int trainPercentage) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(new File(jsonFilePath));

        Map<String, List<List<String>>> conceptData = new HashMap<>();
        Map<String, Integer> conceptCounters = new HashMap<>(); // Zähler für jedes Konzept

        while (parser.nextToken() != JsonToken.START_OBJECT) {
        }

        while (parser.nextToken() != null) {
            if (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
                String currentKey = parser.getCurrentName();

                if (CONCEPTS.contains(currentKey)) {
                    parser.nextToken();
                    if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
                        List<List<String>> images = new ArrayList<>();
                        int colorCount = 0;

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
                                            colorCount++;
                                        }
                                    }
                                }
                            }
                        }

                        conceptData.put(currentKey, images);
                        conceptCounters.put(currentKey, colorCount);
                    }
                }
            }
        }

        System.out.println("Split der Daten pro Konzept:");

        NDManager manager = NDManager.newBaseManager(); // NDManager zur Erstellung von ND-Arrays

        for (String concept : CONCEPTS) {
            List<List<String>> images = conceptData.getOrDefault(concept, new ArrayList<>());
            int totalImages = images.size();

            // Zufälliges Mischen der Daten
            Collections.shuffle(images, new Random());

            int trainCount = Math.round(totalImages * (trainPercentage / 100.0f));
            int testCount = totalImages - trainCount;

            List<List<String>> trainData = images.subList(0, trainCount);
            List<List<String>> testData = images.subList(trainCount, totalImages);

            // NDArray-Konvertierung
            NDArray trainNDArray = createNDArray(manager, trainData);
            NDArray testNDArray = createNDArray(manager, testData);

            // Logging-Ausgabe
            System.out.println("Konzept: " + concept + " NDArrays werden erstellt...");
            System.out.println("Trainings NDArray: " + trainNDArray.size() + " ("+trainNDArray.size()/400+" Bilder)");
            System.out.println("Test NDArray: " + testNDArray.size()+ " ("+testNDArray.size()/400+" Bilder)");

            System.out.println("Bilder in " + concept + ": " + conceptCounters.getOrDefault(concept, 0)+"\n");
        }
    }

    // Konvertiert eine Liste von Farbwertlisten in ein NDArray.
    private static NDArray createNDArray(NDManager manager, List<List<String>> data) {
        int numRows = data.size();
        int numCols = data.isEmpty() ? 0 : data.get(0).size();
        float[][] arrayData = new float[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                try {
                    arrayData[i][j] = Float.parseFloat(data.get(i).get(j));
                } catch (NumberFormatException e) {
                    // Default-Wert für ungültige Zahlen
                    arrayData[i][j] = 0.0f;
                }
            }
        }

        return manager.create(arrayData);
    }
}
