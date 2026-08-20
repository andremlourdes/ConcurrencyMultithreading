package com.andrelourdes.group2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class IoBoundVirtualThreads {
    public static void main(String[] args) {
        // Creates an executor that launches a new virtual thread for each task.
        // There is no virtual thread pooling because they are extremely lightweight.

        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0,100_000).forEach(i -> {
                executor.submit(() -> {
                    // Simulates a blocking network call (I/O).
                    try {
                        System.out.println("Iniciando tarefa I/O-bound" + i + " em " + Thread.currentThread());
                        Thread.sleep(1000); // Simulates 1 second of waiting.
                        System.out.println(" Tarefa I/O-bound" +  i + " concluída.");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                });
            });
        }
    }
}
