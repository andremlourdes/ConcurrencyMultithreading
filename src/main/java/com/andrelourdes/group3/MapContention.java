package com.andrelourdes.group3;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks thread-safe map implementations under high concurrent load.
 *
 * <p>Two map variants are tested with the same workload: {@code Collections.synchronizedMap}
 * (a wrapper that serialises every access with a single lock) and {@code ConcurrentHashMap}
 * (which uses fine-grained segment locking for much better throughput).
 *
 * <p>Each test spawns twice as many threads as available CPU cores. Every thread performs
 * 100,000 interleaved {@code put} and {@code get} operations. Total elapsed time and
 * final map size are printed so the two implementations can be compared directly.
 */
public class MapContention {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Testing Collections.synchronizedMap...");
        runTest(Collections.synchronizedMap(new HashMap<>()));

        System.out.println("\nTesting ConcurrentHashMap...");
        runTest(new ConcurrentHashMap<>());
    }

    private static void runTest(Map<String, Integer> map) throws InterruptedException {
        // Use twice as many threads as available cores to simulate concurrent contention.
        int numThreads = Runtime.getRuntime().availableProcessors() * 2;
        // Each thread performs 100,000 put/get operations.
        int operationsPerThread = 100_000;

        try (ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {
            long startTime = System.nanoTime();

            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = Thread.currentThread().getName() + "-" + j;
                        map.put(key, j);
                        map.get(key);
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            System.out.println("Total time: " + duration + " ms");
            System.out.println("Final map size: " + map.size());
        }
    }
}