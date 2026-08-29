package com.andrelourdes.group3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Demonstrates the classic Producer–Consumer pattern using {@link java.util.concurrent.ArrayBlockingQueue}.
 *
 * <p>Two virtual threads run concurrently:
 * <ul>
 *   <li><b>Producer</b>: generates integers 0–99 and inserts them into the queue.
 *       It blocks automatically when the queue is full (capacity = 10),
 *       providing natural back-pressure.</li>
 *   <li><b>Consumer</b>: removes integers one at a time and processes them.
 *       It blocks automatically when the queue is empty, avoiding busy-waiting.
 *       Processing stops when the sentinel value 99 is received.</li>
 * </ul>
 *
 * <p>The producer runs at twice the speed of the consumer (50 ms vs 100 ms per item),
 * so the queue acts as a buffer that decouples their execution rates.
 */
public class ProducerConsumerBlockingQueue {

    public static void main(String[] args) {
        // A fixed-capacity queue with room for 10 items.
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Producer: generates numbers and puts them into the queue.
            executor.submit(() -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        System.out.println("Producing: " + i);
                        queue.put(i); // Blocks if the queue is full.
                        Thread.sleep(50); // Simulates production time.
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // Consumer: takes numbers from the queue and processes them.
            executor.submit(() -> {
                try {
                    while (true) {
                        Integer value = queue.take(); // Blocks if the queue is empty.
                        System.out.println("Consuming: " + value);
                        Thread.sleep(100); // Simulates processing time.
                        if (value == 99) break; // Stop condition.
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
}