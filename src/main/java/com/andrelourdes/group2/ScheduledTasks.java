package com.andrelourdes.group2;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Demonstrates the three scheduling strategies provided by {@link java.util.concurrent.ScheduledExecutorService}.
 *
 * <ul>
 *   <li><b>schedule</b>: runs a task exactly once after a fixed delay (3 seconds).</li>
 *   <li><b>scheduleAtFixedRate</b>: runs a task repeatedly at a fixed interval (every 5 seconds),
 *       measuring from the <em>start</em> of each execution. If a run takes longer than the
 *       period, the next run starts immediately after it finishes.</li>
 *   <li><b>scheduleWithFixedDelay</b>: runs a task repeatedly with a fixed delay (5 seconds)
 *       measured from the <em>end</em> of the previous execution, guaranteeing a pause
 *       between runs regardless of how long each run takes.</li>
 * </ul>
 *
 * The scheduler runs for 20 seconds for observation, then shuts down via try-with-resources.
 */
public class ScheduledTasks {
     public static void main(String[] args) throws InterruptedException{
         try (ScheduledExecutorService schedule = Executors.newScheduledThreadPool(2)){

             // Task 1: Run once after a 3-second delay.
                schedule.schedule(() -> {
                    System.out.println("One-time task executed at " + LocalDateTime.now());
                }, 3, java.util.concurrent.TimeUnit.SECONDS);

             // Task 2: Run every 5 seconds, with an initial delay of 1 second.
             // scheduleAtFixedRate starts the next execution at the scheduled time,
             // even if the previous execution was delayed.

                schedule.scheduleAtFixedRate(() -> {
                    System.out.println("Periodic task executed at " + LocalDateTime.now());
                    try{
                        Thread.sleep(1000);// Simulates work.
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();

                    }
                }, 1, 5, java.util.concurrent.TimeUnit.SECONDS);

             // Task 3: Run with a fixed delay of 5 seconds between the end of one
             // execution and the start of the next.
                schedule.scheduleWithFixedDelay(() -> {
                    System.out.println("Periodic task (fixed delay) executed at " + LocalDateTime.now());
                    try{
                        Thread.sleep(2000);// Simulates work.
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, 1, 5, java.util.concurrent.TimeUnit.SECONDS);

             // Lets the scheduler run for 20 seconds for observation.
             Thread.sleep(20000);
         } // shutdown() is called here.
     }
}
