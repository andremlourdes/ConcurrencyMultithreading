package com.andrelourdes.group3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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