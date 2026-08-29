package com.andrelourdes.group3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates thread-safe listener management using {@link java.util.concurrent.CopyOnWriteArrayList}.
 *
 * <p>Two threads run concurrently:
 * <ol>
 *   <li>Thread 1 iterates over the listener list and fires 5 events in sequence.</li>
 *   <li>Thread 2 adds a third listener while Thread 1 is still notifying.</li>
 * </ol>
 *
 * <p>{@code CopyOnWriteArrayList} creates a fresh copy of the underlying array on every
 * write, so the iterator in Thread 1 always sees the snapshot it started with and
 * never throws {@link java.util.ConcurrentModificationException}. The newly added
 * Listener C will only receive events fired after it was registered.
 */
public class OnWriteListener {

    // Listener interface.
    interface EventListener {
        void onEvent(String event);
    }

    // Notifier class.
    static class Notifier {
        private final List<EventListener> listeners = new CopyOnWriteArrayList<>();

        public void addListener(EventListener listener) {
            listeners.add(listener);
            System.out.println("Listener added. Current list: " + listeners.size());
        }

        public void notifyListeners(String event) {
            System.out.println("Notifying " + listeners.size() + " listeners about event: " + event);
            // Iteration is safe and does not throw ConcurrentModificationException.
            for (EventListener listener : listeners) {
                listener.onEvent(event);
                try {
                    // Simulates notification work.
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Notifier notifier = new Notifier();
        notifier.addListener(event -> System.out.println("Listener A received: " + event));
        notifier.addListener(event -> System.out.println("Listener B received: " + event));

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            // Thread 1: Continuously notifies listeners.
            executor.submit(() -> {
                for (int i = 0; i < 5; i++) {
                    notifier.notifyListeners("Event " + i);
                }
            });

            // Thread 2: Adds a new listener while notification is in progress.
            executor.submit(() -> {
                try {
                    Thread.sleep(50); // Waits a bit for notification to start.
                    notifier.addListener(event -> System.out.println("Listener C (new) received: " + event));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}