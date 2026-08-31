package com.andrelourdes.group7;

import java.util.stream.IntStream;

/**
 * Demonstrates a BAD use case for parallel streams: I/O-bound operations.
 *
 * <p><b>Technique: When NOT to Use Parallel Streams</b>
 * <ul>
 *   <li>Parallel streams are designed for CPU-bound computations that benefit from
 *       multi-core parallelism.</li>
 *   <li>I/O-bound operations (network calls, file reads, database queries) do NOT benefit
 *       from parallel streams because threads spend most time waiting for I/O to complete.</li>
 *   <li>The overhead of parallel stream coordination and task switching may actually
 *       make I/O operations slower than sequential processing.</li>
 *   <li>For I/O-bound work, use reactive frameworks, async/await patterns, or virtual threads instead.</li>
 * </ul>
 *
 * <p><b>Example:</b> Making HTTP requests with parallel streams adds thread coordination
 * overhead without improving throughput, since all threads will block waiting for responses.
 */
public class ParallelStreamBadUseCaseIO {

    /**
     * Simulates a network I/O operation (e.g., HTTP request).
     * Takes significant time due to network latency.
     */
    static String fetchDataFromApi(int requestId) {
        try {
            Thread.sleep(500); // Simulate network latency.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Data from request " + requestId;
    }

    public static void main(String[] args) {
        System.out.println("BAD USE CASE: Parallel streams for I/O-bound operations.\n");

        // BAD: Using parallel stream for I/O operations.
        // This adds parallelism overhead without benefit because all threads
        // block waiting for network responses.
        long startTime = System.currentTimeMillis();
        IntStream.range(0, 5)
                .parallel() // DON'T do this for I/O operations!
                .forEach(i -> System.out.println(fetchDataFromApi(i)));
        long parallelTime = System.currentTimeMillis() - startTime;

        System.out.println("\nParallel stream time: " + parallelTime + " ms");

        // GOOD: Sequential stream for I/O operations, or use virtual threads/reactive patterns.
        startTime = System.currentTimeMillis();
        IntStream.range(0, 5)
                .forEach(i -> System.out.println(fetchDataFromApi(i)));
        long sequentialTime = System.currentTimeMillis() - startTime;

        System.out.println("Sequential stream time: " + sequentialTime + " ms");
        System.out.println("\nNote: Parallel actually performs similarly or worse due to coordination overhead.");
    }
}

