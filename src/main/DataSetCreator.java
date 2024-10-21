package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DataSetCreator {

	public static final int RANGE = 80; // FeatureVector count per concept: (RANGE * 0.2 + 1)^2

	private final Random random;

	private final Set<FeatureVector> trainingsSet;

	private final Set<FeatureVector> testSet;

	DataSetCreator(int trainingsSetSizePerConcept, long seed) {
		this.random = new Random(seed);

		this.trainingsSet = new HashSet<>();
		this.testSet = new HashSet<>();

		List<String> usedNames = new ArrayList<>();

		for (Concept concept : Concept.values()) {

			for (int i = 0; i < trainingsSetSizePerConcept; i++) {
				int randNum1 = nextRandom();
				int randNum2 = nextRandom();

				String xy = "X" + randNum1 + "Y" + randNum2;

				usedNames.add(xy);

				FeatureVector fv = FileManager.readFile(concept.name() + xy);
				trainingsSet.add(fv);
			}		

			for (int i = -RANGE; i <= RANGE; i += 10) {
				for (int k = -RANGE; k <= RANGE; k += 10) {
					
					String xy = "X" + i + "Y" + k;

					if (!usedNames.contains(xy)) {
						FeatureVector fv = FileManager.readFile(concept.name() + xy);
						testSet.add(fv);
					}			
				}
			}
		}
	}

	private int nextRandom() {
		int randNum = this.random.nextInt(RANGE / 5 + 1);
		randNum -= (RANGE / 10);
		randNum *= 10;
		return randNum;
	}

	Set<FeatureVector> getTrainingsData() {
		return this.trainingsSet;
	}

	Set<FeatureVector> getTestData() {
		return this.testSet;
	}

}