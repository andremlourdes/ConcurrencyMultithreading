package com.andrelourdes.group6;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Demonstrates how to wait for multiple asynchronous tasks to complete using
 * {@link CompletableFuture#allOf(CompletableFuture[])}.
 *
 * <p><b>Technique: Composing Multiple Futures</b>
 * <ul>
 *   <li>{@code CompletableFuture.allOf()} takes multiple futures and returns a single
 *       {@code CompletableFuture<Void>} that completes when all input futures complete.</li>
 *   <li>The returned future does NOT return combined results directly; it only signals
 *       when all tasks are done.</li>
 *   <li>To retrieve results, the original futures must be explicitly joined after
 *       the {@code allOf()} future completes.</li>
 * </ul>
 *
 * <p><b>Key Insight:</b> This example shows a practical pattern for fetching data from
 * multiple sources concurrently and then collecting all results. The {@code allOf()}
 * future acts as a synchronization point before gathering results.
 *
 * <p>Use case: Parallel API calls, batch processing, multi-source data aggregation.
 */
public class AllOf {

    /**
     * Simulates asynchronous data download from a remote source.
     * Random delay between 1-2 seconds represents network latency.
     */
    static CompletableFuture<String> downloadData(String source) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int delay = 1000 + (int) (Math.random() * 1000);
                Thread.sleep(delay);
                System.out.println("Data from " + source + " downloaded in " + delay + "ms.");
            } catch (Exception e) {}
            return "Data from " + source;
        });
    }

    public static void main(String[] args) {
        List<String> sources = List.of("API_1", "API_2", "API_3", "API_4");

        // Create futures for downloading data from each source.
        List<CompletableFuture<String>> futures = sources.stream()
                .map(AllOf::downloadData)
                .collect(Collectors.toList());

        // CompletableFuture.allOf() returns CompletableFuture<Void>.
        // It signals only that all futures completed; it does not return their results.
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        // To obtain results, map the futures using thenApply after allOf() completes.
        // join() is safe here because all futures have completed by this point.
        CompletableFuture<List<String>> allResultsFuture = allDoneFuture.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );

        List<String> results = allResultsFuture.join();
        System.out.println("\nAll downloads completed. Results:");
        results.forEach(System.out::println);
    }
}