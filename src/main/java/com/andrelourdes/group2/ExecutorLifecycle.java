package com.andrelourdes.group2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates the two recommended patterns for shutting down an {@link java.util.concurrent.ExecutorService}.
 *
 * <p><b>Technique: Executor Lifecycle Management</b>
 * <ul>
 *   <li><b>try-with-resources</b> (Java 19+): The most modern and safe approach.
 *       {@code ExecutorService} implements {@code AutoCloseable}, enabling automatic
 *       resource cleanup when the try-block exits, whether normally or via exception.</li>
 *   <li><b>Manual shutdown</b>: For Java versions prior to 19 or complex scenarios.
 *       Gracefully shuts down the executor, waits for running tasks to complete,
 *       and forcefully cancels remaining tasks if necessary.</li>
 * </ul>
 *
 * <p><b>Why Proper Shutdown Matters:</b>
 * <ul>
 *   <li>Prevents thread leaks that would accumulate over time in long-running applications</li>
 *   <li>Allows in-flight tasks to complete gracefully (shutdown pattern)</li>
 *   <li>Ensures forceful cancellation if tasks do not respond within a timeout</li>
 * </ul>
 */
public class ExecutorLifecycle {
    public static void main(String[] args) {
        // Starting with Java 19, ExecutorService implements AutoCloseable.
        // Using try-with-resources is the preferred and safest approach.
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            executor.submit(() -> System.out.println("Task is running."));
        } // executor.shutdown() is called automatically.
        System.out.println("Executor shut down via try-with-resources.");

        // Manual shutdown pattern (for Java < 19 or complex scenarios).
        ExecutorService manualExecutor = Executors.newFixedThreadPool(2);
        try {
            manualExecutor.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Task in the manual executor completed.");
            });
        } finally {
            shutdownAndAwaitTermination(manualExecutor);
        }
        System.out.println("Manual executor shut down.");
    }

    static void shutdownAndAwaitTermination(ExecutorService pool) {
        // Step 1: Disable new task submissions.
        pool.shutdown();
        try {
            // Step 2: Wait up to 60 seconds for existing tasks to complete.
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                // Step 3: If tasks didn't finish, cancel them forcefully.
                pool.shutdownNow();
                // Step 4: Wait again up to 60 seconds for tasks to respond to cancellation.
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("The pool did not terminate.");
            }
        } catch (InterruptedException ie) {
            // If the current thread is interrupted, force shutdown and restore interrupt status.
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}