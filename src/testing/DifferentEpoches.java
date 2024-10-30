package testing;

import java.util.function.Supplier;

import learning.Learner;
import main.Main;
import main.TestResult;

public class DifferentEpoches implements TestAI {

    @Override
    public void testModel(Supplier<Learner> learnerConstructor) {
        double bestAverage = 0;
        TestResult bestResult = null;

        for (int i = 0; i < 10; i++) {
            Learner learner = learnerConstructor.get();
            TestResult result = Main.testAIModel(learner, 19, i, 600);
            System.out.println(result);
            if (result.averageSuccess() > bestAverage) {
                bestAverage = result.averageSuccess();
                bestResult = result;
            }
        }

        System.out.println("Best result:");
        System.out.println(bestResult);
    }
}

