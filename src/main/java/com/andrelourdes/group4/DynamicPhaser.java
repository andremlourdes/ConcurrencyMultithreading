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
public class DynamicPhaser {
}
