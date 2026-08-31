package com.andrelourdes.group2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Demonstrates Java virtual threads (Project Loom, Java 21+) for I/O-bound workloads.
 *
 * <p>100,000 tasks are submitted, each sleeping for 1 second to simulate a blocking
 * network or database call. A new virtual thread is created for every task via
 * {@link java.util.concurrent.Executors#newVirtualThreadPerTaskExecutor()}.
 * Because virtual threads are extremely lightweight (no native thread per task),
 * the JVM can handle all 100,000 concurrently without exhausting OS resources.
 * This pattern would be impractical with a traditional platform-thread pool.
 */
public class IoBoundVirtualThreads {
    public static void main(String[] args) {
        // Create an executor that spawns a new virtual thread for every task.
        // Virtual threads are extremely lightweight, so no pooling is needed.

        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0,100_000).forEach(i -> {
                executor.submit(() -> {
                    // Simulate a blocking network or I/O operation.
                    try {
                        System.out.println("Starting I/O-bound task " + i + " on " + Thread.currentThread());
                        Thread.sleep(1000); // Block for 1 second to simulate I/O.
                        System.out.println("I/O-bound task " + i + " completed.");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                });
            });
        }
    }
}
