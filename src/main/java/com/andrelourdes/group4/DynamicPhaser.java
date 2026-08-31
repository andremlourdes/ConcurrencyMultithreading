package com.andrelourdes.group4;

/**
 * Demonstrates the use of {@link java.util.concurrent.Phaser} for flexible,
 * multi-phase synchronization of an arbitrary number of parties.
 *
 * <p>Unlike {@code CyclicBarrier}, a {@code Phaser}:
 * <ul>
 *   <li>Does not require knowing the number of parties upfront; parties can register
 *       and deregister dynamically during execution.</li>
 *   <li>Supports automatic termination when the number of parties drops to zero.</li>
 *   <li>Allows selective participation: some parties can terminate before others.</li>
 * </ul>
 *
 * <p>Phasers are useful for evolving workloads where thread participation changes
 * over the lifetime of the computation.
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class DynamicPhaser {

    static class Task implements Runnable {
        private final int id;
        private final Phaser phaser;

        Task(int id, Phaser phaser) {
            this.id = id;
            this.phaser = phaser;
            phaser.register(); // Registers this task as a participant.
            System.out.println("Task " + id + " registered. Participants: " + phaser.getRegisteredParties());
        }

        @Override
        public void run() {
            System.out.println("Task " + id + " in Phase " + phaser.getPhase() + ".");
            phaser.arriveAndAwaitAdvance(); // Signals arrival and waits for other participants.

            // Only some tasks continue to the next phase.
            if (id % 2 == 0) {
                System.out.println("Task " + id + " continuing to Phase " + phaser.getPhase() + ".");
                phaser.arriveAndAwaitAdvance();
                System.out.println("Task " + id + " completed all phases.");
                phaser.arriveAndDeregister(); // Signals arrival and deregisters.
            } else {
                System.out.println("Task " + id + " completing and deregistering in Phase " + phaser.getPhase() + ".");
                phaser.arriveAndDeregister(); // Signals arrival, deregisters, and does not wait.
            }
        }
    }

    public static void main(String[] args) {
        Phaser phaser = new Phaser(1); // 1 for the main thread.

        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            for (int i = 0; i < 4; i++) {
                executor.submit(new Task(i, phaser));
            }

            System.out.println("Waiting for all tasks to complete Phase 0...");
            phaser.arriveAndAwaitAdvance();
            System.out.println("Phase 0 completed. Remaining participants: " + phaser.getRegisteredParties());

            System.out.println("Waiting for remaining tasks to complete Phase 1...");
            phaser.arriveAndAwaitAdvance();
            System.out.println("Phase 1 completed. Remaining participants: " + phaser.getRegisteredParties());

            phaser.arriveAndDeregister(); // Main thread deregisters.
            System.out.println("Phaser terminated: " + phaser.isTerminated());
        }
    }
}
