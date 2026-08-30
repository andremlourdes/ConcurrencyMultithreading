package com.andrelourdes.group4;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

public class SemaphoreResourcePool {

    // Simulates a connection pool with a limited number of permits.
    static class ConnectionPool {
        private final Semaphore semaphore;

        public ConnectionPool(int maxConnections) {
            // Setting 'true' makes the semaphore "fair", serving threads in arrival order.
            this.semaphore = new Semaphore(maxConnections, true);
        }

        public void useConnection(int threadId) throws InterruptedException {
            semaphore.acquire(); // Acquires a permit. Blocks if none are available.
            try {
                System.out.println("Thread " + threadId + " acquired a connection.");
                Thread.sleep(2000); // Simulates connection usage.
                System.out.println("Thread " + threadId + " releasing the connection.");
            } finally {
                semaphore.release(); // Releases the permit. ESSENTIAL to place in finally.
            }
        }
    }

    public static void main(String[] args) {
        ConnectionPool pool = new ConnectionPool(3); // Only 3 concurrent connections.

        // Using virtual threads to simulate many clients trying to access the pool.
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