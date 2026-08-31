package com.andrelourdes.group8;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Demonstrates massive concurrency using Project Loom's virtual threads.
 *
 * <p><b>Technique: Virtual Threads for High-Concurrency I/O Workloads</b>
 * <ul>
 *   <li>Virtual threads are lightweight threads managed by the Java runtime, not the OS.</li>
 *   <li>Creating millions of virtual threads is feasible; attempting millions of platform threads is not.</li>
 *   <li>Each virtual thread occupies minimal memory (~1 KB vs ~1 MB for platform threads).</li>
 *   <li>When a virtual thread blocks on I/O, it is unmounted from the carrier thread,
 *       allowing other virtual threads to use the carrier thread without context switching overhead.</li>
 *   <li>This enables a single carrier thread (OS thread) to multiplex thousands of virtual threads.</li>
 * </ul>
 *
 * <p><b>Key Benefit:</b> Scales I/O-bound applications from thousands to millions of
 * concurrent operations without the complexity of reactive programming or async/await callbacks.
 * Virtual threads make concurrent code look and feel like sequential code.
 *
 * <p><b>Use Case:</b> Web servers handling millions of concurrent clients, bulk data processing,
 * fan-out requests to multiple services.
 */
public class MassiveConcurrencyWithVirtualThreads {

    /**
     * Simulates an I/O-bound operation (e.g., network request, database query).
     */
    static void simulateIOWork(int taskId) {
        try {
            Thread.sleep(100); // Simulate I/O latency.
            System.out.println("Task " + taskId + " completed on thread " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        System.out.println("Demonstrating massive concurrency with virtual threads.\n");

        int numberOfTasks = 10_000;

        // With platform threads, attempting 10,000 concurrent tasks would be impractical.
        // With virtual threads, it's trivial and efficient.
        long startTime = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, numberOfTasks)
                    .forEach(i -> executor.submit(() -> simulateIOWork(i)));
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nAll " + numberOfTasks + " tasks completed in " + (endTime - startTime) + " ms");
        System.out.println("Average time per task: " + ((double) (endTime - startTime) / numberOfTasks) + " ms");
    }
}

