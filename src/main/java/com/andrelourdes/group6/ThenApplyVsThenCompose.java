package com.andrelourdes.group6;

import java.util.concurrent.CompletableFuture;

/**
 * Demonstrates the key difference between {@link CompletableFuture#thenApply(java.util.function.Function)}
 * and {@link CompletableFuture#thenCompose(java.util.function.Function)}.
 *
 * <p><b>Technique: Chaining Asynchronous Operations</b>
 * <ul>
 *   <li><b>thenApply():</b> Transforms the result using a synchronous function.
 *       Returns {@code CompletableFuture<R>} where the transformation wraps the result.
 *       Use when mapping a value to another value.</li>
 *   <li><b>thenCompose():</b> Chains multiple async operations. The function itself
 *       returns a {@code CompletableFuture}, which is automatically flattened.
 *       Avoids nested {@code CompletableFuture<CompletableFuture<T>>} structures.</li>
 * </ul>
 *
 * <p><b>Rule of Thumb:</b>
 * <ul>
 *   <li>Use {@code thenApply()} when transforming a value synchronously.</li>
 *   <li>Use {@code thenCompose()} when chaining multiple async operations.</li>
 * </ul>
 *
 * <p>This pattern is essential for building readable, non-nested async call chains.
 */
public class ThenApplyVsThenCompose {

    /**
     * Simulates fetching user data asynchronously.
     */
    static CompletableFuture<String> fetchUser(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            return "User " + userId;
        });
    }

    /**
     * Simulates fetching user profile asynchronously based on user data.
     */
    static CompletableFuture<String> fetchProfile(String userName) {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            return userName + " profile data";
        });
    }

    public static void main(String[] args) {
        // Pattern 1: Using thenApply() for synchronous transformation.
        // This results in CompletableFuture<CompletableFuture<String>> if fetchProfile returned a future.
        CompletableFuture<String> wrongWay = fetchUser(1)
                .thenApply(user -> user + " (transformed synchronously)");

        System.out.println("ThenApply result: " + wrongWay.join());

        // Pattern 2: Using thenCompose() to chain async operations.
        // The returned CompletableFuture is automatically flattened.
        CompletableFuture<String> rightWay = fetchUser(1)
                .thenCompose(user -> fetchProfile(user));

        System.out.println("ThenCompose result: " + rightWay.join());
    }
}

