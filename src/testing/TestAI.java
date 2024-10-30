package testing;

import java.util.function.Supplier;

import learning.Learner;

public interface TestAI {
    
    /**
     * Tests the provided learner with different input and prints some nice results.
     * @param learnerConstructor
     */
    void testModel(Supplier<Learner> learnerConstructor);

}
