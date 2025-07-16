package com.brandon.globaleconomy.util;

/**
 * Centralized configuration for enabling or disabling debug output across the plugin.
 */
public class DebugConfig {

    // Master debug toggle
    public static final boolean DEBUG_MODE = true;

    // Enables pathing and movement logging for NPCs (e.g., residents, workers)
    public static final boolean SHOW_NPC_PATHING = true;

    // Enables task execution logs like performWork() for each worker
    public static final boolean SHOW_WORKER_TASKS = true;

    // Logs economy-related changes, like trade or inventory shifts
    public static final boolean SHOW_ECONOMY_EVENTS = false;

    // Logs city creation, deletion, and interactions
    public static final boolean SHOW_CITY_EVENTS = false;

    // Logs interactions and behavior of builder NPCs
    public static final boolean SHOW_BUILDER_ACTIONS = false;

    // Use for logging experimental features or dev-only hooks
    public static final boolean SHOW_DEV_EXPERIMENTS = false;

    /**
     * Utility to conditionally print debug messages.
     */
    public static void log(String message, boolean condition) {
        if (DEBUG_MODE && condition) {
            System.out.println("[Debug] " + message);
        }
    }
}
