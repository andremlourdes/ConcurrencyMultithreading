package com.andrelourdes.group4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Demonstrates the use of {@link java.util.concurrent.CyclicBarrier} for synchronizing
 * multiple worker threads across distinct phases of computation.
 *
 * <p>Three workers execute two phases sequentially:
 * <ol>
 *   <li><b>Phase 1 (Data Processing)</b>: Each worker performs work at different speeds
 *       and waits at the barrier. Once all workers arrive, a barrier action prints a message.</li>
 *   <li><b>Phase 2 (Data Validation)</b>: Similarly, workers complete their work and wait
 *       at the barrier again before proceeding.</li>
 * </ol>
 *
 * <p>The {@code CyclicBarrier} can be reused; after all parties arrive at the barrier,
 * it resets automatically and is ready for the next phase. This differs from {@code CountDownLatch},
 * which cannot be reused.
 */
public class CyclicBarrierSolver  {

    static class Worker implements Runnable {
        private final int id;
        private final CyclicBarrier barrier;

        public Worker(int id, CyclicBarrier barrier) {
            this.id = id;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                // Phase 1: Data processing.
                System.out.println("Worker " + id + " starting Phase 1.");
                Thread.sleep(1000 + (id * 500));
                System.out.println("Worker " + id + " completed Phase 1, waiting at barrier.");
                barrier.await();

                // Phase 2: Data validation.
                System.out.println("Worker " + id + " starting Phase 2.");
                Thread.sleep(1000 + (id * 500));
                System.out.println("Worker " + id + " completed Phase 2, waiting at barrier.");
                barrier.await();

                System.out.println("Worker " + id + " completed work.");

            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        int numberOfWorkers = 3;

        // Barrier action: executed when all threads arrive.
        Runnable barrierAction = () -> System.out.println("\n--- Barrier broken! All workers completed the phase. Next phase started. ---\n");
        CyclicBarrier barrier = new CyclicBarrier(numberOfWorkers, barrierAction);

        try (ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkers)) {
            for (int i = 0; i < numberOfWorkers; i++) {
                executor.submit(new Worker(i, barrier));
            }
        }
    }
}
