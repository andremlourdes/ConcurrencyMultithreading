package com.andrelourdes.group2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        // Disables new tasks from being submitted.
        pool.shutdown();
        try {
            // Waits a reasonable amount of time for existing tasks to finish.
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                // Cancels currently running tasks.
                pool.shutdownNow();
                // Waits a reasonable amount of time for tasks to respond to cancellation.
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("The pool did not terminate.");
            }
        } catch (InterruptedException ie) {
            // Re-cancels if the current thread is interrupted.
            pool.shutdownNow();
            // Preserves the interruption status.
            Thread.currentThread().interrupt();
        }
    }
}