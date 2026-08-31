package com.andrelourdes.group5;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * Demonstrates the Fork/Join pattern for solving large-scale computational problems
 * using divide-and-conquer strategy with parallel execution.
 *
 * <p><b>Technique: Fork/Join Pattern (Divide-and-Conquer)</b>
 * <ul>
 *   <li>Recursively splits a large array sum into smaller independent subtasks</li>
 *   <li>Uses a threshold (10,000 elements) to determine when to compute directly</li>
 *   <li>Employs work-stealing scheduler to distribute load across processor cores</li>
 *   <li>Combines results from child tasks using the join() method</li>
 * </ul>
 *
 * <p><b>Key Optimization:</b> The code uses an asymmetric fork/join pattern:
 * <ul>
 *   <li>Left subtask is forked (sent to thread pool for async execution)</li>
 *   <li>Right subtask is computed on the current thread (avoids unnecessary queueing)</li>
 *   <li>Results are combined after both subtasks complete</li>
 * </ul>
 *
 * <p>This example calculates the sum of integers from 1 to 1,000,000 using the
 * shared {@link ForkJoinPool#commonPool()}, demonstrating both computation and
 * performance measurement.
 */
public class ForkJoinSum {

    /**
     * Recursive task that computes the sum of a portion of an array using divide-and-conquer.
     * When the array segment is small enough (<= 10,000 elements), computes the sum directly.
     * Otherwise, splits the segment into two halves and processes them in parallel.
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
            int length = end - start;
            if (length <= THRESHOLD) {
                // Base case: array segment is small enough to compute directly.
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            }

            // Recursive case: divide the array into two halves.
            int mid = start + length / 2;
            SumTask leftTask = new SumTask(array, start, mid);
            SumTask rightTask = new SumTask(array, mid, end);

            // Optimization: fork the left task for async execution while computing right on current thread.
            // This avoids queueing both tasks and allows the current thread to do useful work.
            leftTask.fork(); // Send left task for asynchronous execution.
            long rightResult = rightTask.compute(); // Execute right task on current thread.
            long leftResult = leftTask.join(); // Wait for left task result.

            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) {
        long[] numbers = LongStream.rangeClosed(1, 1_000_000).toArray();

        // Use the shared common pool (distributed across all JVM instances).
        try (ForkJoinPool pool = ForkJoinPool.commonPool()) {
            long startTime = System.currentTimeMillis();
            long result = pool.invoke(new SumTask(numbers, 0, numbers.length));
            long endTime = System.currentTimeMillis();

            System.out.println("Sum result: " + result);
            System.out.println("Execution time: " + (endTime - startTime) + " ms");
        }
    }
}