package com.andrelourdes.group7;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Demonstrates a BAD use case for parallel streams: stateful operations.
 *
 * <p><b>Technique: Avoiding Stateful Lambda Expressions in Parallel Streams</b>
 * <ul>
 *   <li>Parallel streams distribute elements across multiple threads without a defined order.</li>
 *   <li>If the operation is stateful (depends on shared mutable state or ordering),
 *       parallel execution produces unpredictable and often incorrect results.</li>
 *   <li>In this example, a shared {@code List} is modified by multiple threads,
 *       causing data races and lost updates.</li>
 *   <li>The output differs on each run due to race conditions and thread interleaving.</li>
 * </ul>
 *
 * <p><b>Rule:</b> Stateful operations violate the parallel stream contract. Operations
 * must be stateless, non-interfering, and associative to work correctly with parallel streams.
 */
public class ParallelStreamBadUseCaseStateful {

    public static void main(String[] args) {
        System.out.println("BAD USE CASE: Stateful operations in parallel streams.\n");

        // BAD: Shared mutable state in parallel stream (data race).
        List<String> results = new ArrayList<>();
        IntStream.range(0, 10)
                .parallel()
                .forEach(i -> results.add("Number: " + i)); // Race condition!

        System.out.println("Parallel results (potentially incomplete or reordered):");
        results.forEach(System.out::println);
        System.out.println("Size: " + results.size() + " (expected 10)\n");

        // GOOD: Use collect() to safely combine results in parallel streams.
        System.out.println("Using collect() for safe parallel aggregation:");
        List<String> safeResults = IntStream.range(0, 10)
                .parallel()
                .mapToObj(i -> "Number: " + i)
                .collect(java.util.stream.Collectors.toList());

        safeResults.forEach(System.out::println);
        System.out.println("Size: " + safeResults.size() + " (guaranteed 10)");
    }
}

