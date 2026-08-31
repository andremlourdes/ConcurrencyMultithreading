package com.andrelourdes.group7;

import java.util.stream.LongStream;

/**
 * Demonstrates a GOOD use case for parallel streams: CPU-bound computations.
 *
 * <p><b>Technique: Parallel Streams for Computational Workloads</b>
 * <ul>
 *   <li>Parallel streams excel at CPU-bound operations that benefit from multi-core execution.</li>
 *   <li>This example computes the sum of a large range of integers using parallel reduction.</li>
 *   <li>Each thread independently processes a subset of the range and computes a partial sum.</li>
 *   <li>Results are then combined (reduced) into a final sum using an associative operation.</li>
 *   <li>The parallelism level is automatically determined by the common thread pool.</li>
 * </ul>
 *
 * <p><b>When Parallel Streams Help:</b>
 * <ul>
 *   <li>Large datasets that benefit from multi-core processing</li>
 *   <li>CPU-intensive computations (mathematical calculations, transformations, aggregations)</li>
 *   <li>Operations that are stateless and associative (combining results in any order yields same result)</li>
 * </ul>
 *
 * <p>This is an ideal use case for parallel streams because the operation is:
 * <ul>
 *   <li>Stateless: no shared mutable state</li>
 *   <li>Non-interfering: no side effects</li>
 *   <li>Associative: sum(a, sum(b, c)) == sum(sum(a, b), c)</li>
 * </ul>
 */
public class ParallelStreamGoodUseCase {

    /**
     * Simulates a CPU-intensive operation on a single value.
     */
    static long expensiveComputation(long value) {
        long result = value;
        for (int i = 0; i < 1000; i++) {
            result = result * 2 - 1; // CPU-intensive calculation
        }
        return result;
    }

    public static void main(String[] args) {
        long rangeSize = 1_000_000;

        // Sequential processing.
        long sequentialStart = System.currentTimeMillis();
        long sequentialSum = LongStream.rangeClosed(1, rangeSize)
                .map(ParallelStreamGoodUseCase::expensiveComputation)
                .sum();
        long sequentialTime = System.currentTimeMillis() - sequentialStart;

        System.out.println("Sequential sum: " + sequentialSum);
        System.out.println("Sequential time: " + sequentialTime + " ms\n");

        // Parallel processing.
        long parallelStart = System.currentTimeMillis();
        long parallelSum = LongStream.rangeClosed(1, rangeSize)
                .parallel()
                .map(ParallelStreamGoodUseCase::expensiveComputation)
                .sum();
        long parallelTime = System.currentTimeMillis() - parallelStart;

        System.out.println("Parallel sum: " + parallelSum);
        System.out.println("Parallel time: " + parallelTime + " ms");
        System.out.println("Speedup: " + (double) sequentialTime / parallelTime + "x");
    }
}

