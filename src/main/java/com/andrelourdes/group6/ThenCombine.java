package com.andrelourdes.group6;

import java.util.concurrent.CompletableFuture;

/**
 * Demonstrates combining the results of two independent asynchronous operations
 * using {@link CompletableFuture#thenCombine(CompletableFuture, java.util.function.BiFunction)}.
 *
 * <p><b>Technique: Combining Multiple Async Results</b>
 * <ul>
 *   <li>{@code thenCombine()} waits for two {@code CompletableFuture}s to complete independently.</li>
 *   <li>When both complete, it applies a combining function to their results.</li>
 *   <li>The two futures run in parallel; there's no ordering dependency between them.</li>
 *   <li>Useful for operations like merging data from two sources or performing calculations
 *       that require results from multiple async tasks.</li>
 * </ul>
 *
 * <p><b>When to Use:</b>
 * <ul>
 *   <li>Two independent async operations that need to be combined.</li>
 *   <li>Both results are needed before producing the final output.</li>
 *   <li>Order of completion doesn't matter (both run in parallel).</li>
 * </ul>
 *
 * <p>Contrast with {@code thenCompose()}, which chains dependent async operations.
 */
public class ThenCombine {

    /**
     * Simulates fetching temperature data asynchronously from a weather service.
     */
    static CompletableFuture<Integer> fetchTemperature() {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(800); } catch (InterruptedException e) {}
            return 25; // degrees Celsius
        });
    }

    /**
     * Simulates fetching humidity data asynchronously from a weather service.
     */
    static CompletableFuture<Integer> fetchHumidity() {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(600); } catch (InterruptedException e) {}
            return 65; // percentage
        });
    }

    public static void main(String[] args) {
        // Fetch temperature and humidity independently (both run in parallel).
        // When both complete, combine their results into a weather summary.
        CompletableFuture<String> weatherSummary = fetchTemperature()
                .thenCombine(fetchHumidity(), (temp, humidity) ->
                    String.format("Weather: %d deg C, Humidity: %d%%", temp, humidity)
                );

        System.out.println(weatherSummary.join());
    }
}

