package main;

import java.util.Map;

public record TestResult(
        long learnTime,
        long testTime,
        int epoches,
        int seed,
        int trainingsSize,
        int testSize,
        int trainingsSizePerConcept,
        int testSizePerConcept,
        Map<Concept, Double> successCount,
        double averageSuccess) {
}
