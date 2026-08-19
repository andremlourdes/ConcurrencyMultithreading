package com.andrelourdes.group2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CpuBoundTasks {
    public static void main(String[] args) {
        // The number of threads is sized according to the processor cores.
        int coreCount = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of CPU cores: " + coreCount);

        // try-with-resources ensures the executor is shut down.
        try (ExecutorService executor = Executors.newFixedThreadPool(coreCount)) {
            // Submits N CPU-bound tasks.
            for (int i = 1; i < coreCount * 2; i++) {
                final int taskNumber = i;
                executor.submit(() -> {
                    System.out.println("Starting CPU-bound task " + taskNumber + " on " + Thread.currentThread());
                    // Simulates a computationally intensive workload.
                    long result = performIIntensiveCalculation();
                    System.out.println("CPU-bound task " + taskNumber + " completed with result: " + result + " on " + Thread.currentThread());
                });
            }
        }
    } // executor.shutdown() is called automatically here.

    private static long performIIntensiveCalculation() {
        // Simulates a CPU-intensive calculation.
        long sum = 0;
        for (long i = 0; i < 1_000_000_000L; i++) {
            sum += i;
        }
        return sum;
    }
}
