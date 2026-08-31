package com.andrelourdes.group3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Demonstrates the classic Producer-Consumer pattern using {@link java.util.concurrent.ArrayBlockingQueue}.
 *
 * <p><b>Technique: Producer-Consumer Pattern with Blocking Queues</b>
 * <ul>
 *   <li><b>Producer:</b> Generates data and puts it into a bounded queue.
 *       Automatically blocks when the queue is full, providing natural back-pressure.</li>
 *   <li><b>Consumer:</b> Removes data from the queue and processes it.
 *       Automatically blocks when the queue is empty, avoiding busy-waiting.</li>
 *   <li><b>Decoupling:</b> Queue acts as a buffer that decouples production rate from consumption rate,
 *       enabling different threads to work at different speeds without coordination overhead.</li>
 * </ul>
 *
 * <p><b>Execution Model:</b>
 * <ul>
 *   <li>Two virtual threads run concurrently using {@code newVirtualThreadPerTaskExecutor()}.</li>
 *   <li>Producer generates integers 0-99 at 50 ms intervals (20 items/second).</li>
 *   <li>Consumer processes integers at 100 ms intervals (10 items/second).</li>
 *   <li>The queue (capacity = 10) buffers the rate difference between producer and consumer.</li>
 *   <li>Processing stops when the sentinel value 99 is consumed.</li>
 * </ul>
 *
 * <p>This pattern is fundamental for decoupled, asynchronous processing in concurrent systems.
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