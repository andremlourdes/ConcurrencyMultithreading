package com.andrelourdes.group4;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Demonstrates the use of {@link java.util.concurrent.CountDownLatch} for coordinating
 * the startup of multiple services.
 *
 * <p>Three services (Database, Cache, and Messaging) start concurrently, each taking
 * a different amount of time (3, 5, and 7 seconds respectively). The main thread
 * blocks on {@code latch.await()} until all services have finished initializing
 * (i.e., the latch counter reaches zero). Once all services are ready, the application
 * proceeds to normal operation.
 */
public class ServiceStartupLatch {

    static class ServiceInitializer implements Runnable {
        private final String serviceName;
        private final CountDownLatch latch;
        private final int startupTime;

        public ServiceInitializer(String serviceName, int startupTime, CountDownLatch latch) {
            this.serviceName = serviceName;
            this.startupTime = startupTime;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                System.out.println("Initializing " + serviceName + "...");
                Thread.sleep(startupTime);
                System.out.println(serviceName + " initialized.");
                latch.countDown(); // Decrements the latch counter.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int numberOfServices = 3;
        CountDownLatch startupLatch = new CountDownLatch(numberOfServices);

        try (ExecutorService executor = Executors.newFixedThreadPool(numberOfServices)) {
            executor.submit(new ServiceInitializer("DatabaseService", 3000, startupLatch));
            executor.submit(new ServiceInitializer("CacheService", 5000, startupLatch));
            executor.submit(new ServiceInitializer("MessagingService", 7000, startupLatch));

            System.out.println("Main thread waiting for services initialization...");
            startupLatch.await(); // Blocks until counter reaches zero.

            System.out.println("All services have been initialized. Application is ready!");
        }
    }
}