package testing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import learning.Learner;
import main.Main;
import main.TestResult;

public class DifferentEpoches implements TestAI {

    @Override
    public void testModel(Supplier<Learner> learnerConstructor) {
        double bestAverage = 0;
        TestResult bestResult = null;
        List<Double> listOfBestAverages = new ArrayList<>();

        for (int i = 1; i < 10; i++) {
            Learner learner = learnerConstructor.get();
            TestResult result = Main.testAIModel(learner, 19, i, 600);
            System.out.println(result);

            listOfBestAverages.add(result.averageSuccess());

            if (result.averageSuccess() > bestAverage) {
                bestAverage = result.averageSuccess();
                bestResult = result;
            }
        }

        double sum = 0;
        for(double value : listOfBestAverages){
            sum += value;
        }
        double meanOfAverageSuccesses = sum / listOfBestAverages.size();

        System.out.println("Best result:");
        System.out.println(bestResult);
        System.out.println("List with all average successes:");
        System.out.println(listOfBestAverages);
        System.out.println("Mean average successes:");
        System.out.println(meanOfAverageSuccesses);
    }
}

