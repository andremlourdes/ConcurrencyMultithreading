package com.andrelourdes.group4;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

/**
 * Demonstrates the use of {@link java.util.concurrent.Semaphore} as a resource pool manager.
 *
 * <p>A semaphore is initialized with a fixed number of permits (resources). Threads
 * acquire a permit before accessing a shared resource and release it when done.
 * If no permits are available, threads block until one becomes available.
 *
 * <p>This pattern is useful for limiting concurrent access to a fixed-size resource
 * pool (e.g., database connections, thread pools, or bandwidth-limited services).
 */

public class SemaphoreResourcePool {

    // Simulates a connection pool with a limited number of permits.
    static class ConnectionPool {
        private final Semaphore semaphore;

        public ConnectionPool(int maxConnections) {
            // Setting 'true' makes the semaphore "fair", serving threads in arrival order.
            this.semaphore = new Semaphore(maxConnections, true);
        }

        public void useConnection(int threadId) throws InterruptedException {
            semaphore.acquire(); // Acquire a permit; blocks if none are available.
            try {
                System.out.println("Thread " + threadId + " acquired a connection.");
                Thread.sleep(2000); // Simulate using the connection.
                System.out.println("Thread " + threadId + " releasing the connection.");
            } finally {
                semaphore.release(); // Always release in finally to ensure permits are returned.
            }
        }
    }

    public static void main(String[] args) {
        ConnectionPool pool = new ConnectionPool(3); // Limit to 3 concurrent connections.

        // Use virtual threads to simulate many concurrent clients accessing the pool.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10).forEach(i ->
                    executor.submit(() -> {
                        try {
                            pool.useConnection(i);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    })
            );
        }
    }
}