package com.andrelourdes.group6;

import java.util.concurrent.CompletableFuture;

/**
 * Demonstrates error handling in {@link CompletableFuture} using
 * {@code exceptionally()} and {@code handle()} methods.
 *
 * <p><b>Technique: Exception Handling in Async Chains</b>
 * <ul>
 *   <li><b>exceptionally():</b> Recovers from an exception by providing a fallback value.
 *       Used when you need to substitute a result in case of failure.</li>
 *   <li><b>handle():</b> Handles both success and failure. Accepts both the result
 *       (if successful) and the exception (if failed), providing maximum flexibility.</li>
 *   <li>These methods allow graceful degradation without propagating exceptions up the chain.</li>
 * </ul>
 *
 * <p><b>Key Pattern:</b> Async code can fail at any point. Proper error handling ensures
 * that exceptions don't silently propagate or cause the application to hang.
 */
public class ErrorHandling {

    /**
     * Simulates a task that may fail randomly.
     * 50% chance of success, 50% chance of throwing an exception.
     */
    static CompletableFuture<String> unreliableTask() {
        return CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Task failed unexpectedly!");
            }
            return "Task completed successfully.";
        });
    }

    public static void main(String[] args) throws InterruptedException {
        // Pattern 1: Using exceptionally() to provide a fallback value.
        CompletableFuture<String> withFallback = unreliableTask()
                .exceptionally(ex -> "Error: " + ex.getMessage());

        // Pattern 2: Using handle() to process both success and failure.
        CompletableFuture<String> withHandle = unreliableTask()
                .handle((result, ex) -> {
                    if (ex != null) {
                        return "Handled error: " + ex.getMessage();
                    }
                    return result;
                });

        System.out.println("Result with fallback: " + withFallback.join());
        System.out.println("Result with handle: " + withHandle.join());
    }
}

