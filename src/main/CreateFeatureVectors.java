package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import featureExtraction.ColorCount;

/**
 * 1445 FVs per concept
 */
public class CreateFeatureVectors {

    public static final int DEFAULT_IMAGE_WIDTH = 160;

    public static final int DEFAULT_IMAGE_HEIGHT = 120;

    CreateFeatureVectors() {
        for (Concept concept : Concept.values())
            new Thread(() -> createFeatureVectors(concept)).start();
    }

    FeatureVector createFeatureVector(BufferedImage image, Concept concept) {
        // Generates clusteringCount times the number of feature extractors vectors
        return new FeatureVectorGenerator(image, 9, concept, Arrays.asList(
                new ColorCount(Color.BLUE),
                new ColorCount(Color.RED),
                new ColorCount(Color.YELLOW)));
    }

    void createFeatureVectors(Concept concept) {
        System.out.println("Create vectors for " + concept.name());
        for (int i = -DataSetCreator.RANGE; i <= DataSetCreator.RANGE; i += 10) {
            for (int k = -DataSetCreator.RANGE; k <= DataSetCreator.RANGE; k += 10) {
                for (int b = -2; b <= 2; b++) {
                    String bStr = Util.getBrightness(b);
                    String xy = "X" + i + "Y" + k;
                    FeatureVector vector = createFeatureVector(
                            getImage("Verkehrszeichen/" + concept.name() + "/" + bStr + "/80x60/" + xy + ".bmp"),
                            concept);
                    FileManager.writeFile(vector, concept.name() + bStr + xy);
                }
            }
        }
    }

    BufferedImage getImage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            BufferedImage scaled = Util.scale(image, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
            BufferedImage cropped = Util.crop(scaled);
            return cropped;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
