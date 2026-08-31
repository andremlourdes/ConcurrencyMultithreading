package com.andrelourdes.group5;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Demonstrates the Fork/Join framework by showing how to use both the
 * shared {@link ForkJoinPool#commonPool()} and a custom {@link ForkJoinPool}.
 *
 * <p><b>Technique: Fork/Join Pattern</b>
 * <ul>
 *   <li>Recursively divides a large problem into smaller subproblems (fork)</li>
 *   <li>Solves each subproblem independently and in parallel</li>
 *   <li>Combines the results of all subproblems (join)</li>
 *   <li>Uses work-stealing to balance load across all available threads</li>
 * </ul>
 *
 * <p>This example demonstrates:
 * <ol>
 *   <li>Using {@code ForkJoinPool.commonPool()} (shared across the JVM)</li>
 *   <li>Creating a custom {@code ForkJoinPool} to isolate workloads and avoid contention</li>
 *   <li>How to configure parallelism levels for different scenarios</li>
 * </ol>
 */
public class ForkJoinPoolsUsage {

    /**
     * Recursive task that computes the sum of array elements using divide-and-conquer.
     * Uses a threshold to determine when to switch from recursive splitting to direct computation.
     */
    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;
        private final long[] array;
        private final int start;
        private final int end;

        public SumTask(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                // Base case: problem is small enough to compute directly.
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            }

            // Recursive case: divide the problem into two halves.
            int mid = start + (end - start) / 2;
            SumTask left = new SumTask(array, start, mid);
            SumTask right = new SumTask(array, mid, end);
            left.fork(); // Submit left task for asynchronous execution.
            long rightResult = right.compute(); // Execute right task on current thread.
            long leftResult = left.join(); // Wait for left task to complete.
            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) {
        long[] numbers = new long[1_000_000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }

        // 1. Using the shared common pool across the JVM.
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        System.out.println("Common pool parallelism level: " + commonPool.getParallelism());
        long resultCommon = commonPool.invoke(new SumTask(numbers, 0, numbers.length));
        System.out.println("Result (common pool): " + resultCommon);

        // 2. Using a custom pool with explicit parallelism level.
        // Ideal for isolating workloads and avoiding contention on the common pool.
        int customParallelism = 4;
        try (ForkJoinPool customPool = new ForkJoinPool(customParallelism)) {
            System.out.println("Custom pool parallelism level: " + customPool.getParallelism());
            long resultCustom = customPool.invoke(new SumTask(numbers, 0, numbers.length));
            System.out.println("Result (custom pool): " + resultCustom);
        }
    }
}