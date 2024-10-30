package testing;

import java.util.function.Supplier;

import learning.Learner;
import main.Main;
import main.TestResult;

public class DifferentSeeds implements TestAI {

    @Override
    public void testModel(Supplier<Learner> learnerConstructor) {
        double bestAverage = 0;
        TestResult bestResult = null;

        for (int i = 0; i < 20; i++) {
            Learner learner = learnerConstructor.get();
            TestResult result = Main.testAIModel(learner, i, 3, 600);
            if (result.averageSuccess() > bestAverage) {
                bestAverage = result.averageSuccess();
                bestResult = result;
            }
        }

        System.out.println("Best result:");
        System.out.println(bestResult);
    }

}
