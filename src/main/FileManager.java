package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileManager {

    private static String getPath(String name) {
        return "featureVectors/" + name + ".fv";
    }

    static void writeFile(FeatureVector featureVector, String name) {
        try (FileOutputStream fileOut = new FileOutputStream(getPath(name));
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(featureVector);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    static FeatureVector readFile(String name) {
        FeatureVector featureVector = null;
        try (FileInputStream fileIn = new FileInputStream(getPath(name));
                ObjectInputStream in = new ObjectInputStream(fileIn)) {
            featureVector = (FeatureVector) in.readObject();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }
        return featureVector;
    }

}