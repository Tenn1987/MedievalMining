package com.brandon.globaleconomy.city.claims;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimManager {
    // Map of chunk coordinate keys to city names (for persistence or quick save).
    // Key format: "world:x:z" -> Value: cityName
    private final Map<String, String> chunkClaims = new ConcurrentHashMap<>();

    // The radius (in chunks) around a city's center to claim (e.g., radius 1 means a 3x3 area)
    private final int chunkClaimRadius;

    // Maps each Chunk to the City that has claimed it (for quick lookup of city by chunk)
    private final Map<Chunk, City> chunkToCityMap = new ConcurrentHashMap<>();

    // Maps each City to the set of Chunks it has claimed (for quick lookup of all chunks by city)
    private final Map<City, Set<Chunk>> cityToChunksMap = new ConcurrentHashMap<>();

    public ClaimManager(int chunkClaimRadius) {
        this.chunkClaimRadius = chunkClaimRadius;
        // Note: If claims are loaded from storage at startup, you should populate chunkToCityMap
        // and cityToChunksMap here (or via a separate load method) using chunkClaims data.
    }

    /**
     * Attempts to claim a single chunk for a city.
     * @param chunk The Chunk to claim.
     * @param city  The City claiming the chunk.
     * @return true if claim was successful, false if the chunk was already claimed.
     */
    public boolean claimChunk(Chunk chunk, City city) {
        // Ensure the chunk is not already claimed by another city (no overlap allowed).
        if (chunkToCityMap.containsKey(chunk)) {
            return false;  // Chunk is already claimed by some city, cannot claim it.
        }
        chunkToCityMap.put(chunk, city);
        // Atomically add the chunk to the chunk->city map.
        // Using putIfAbsent for thread-safety in case of concurrent claims.
        City previous = ((ConcurrentHashMap<Chunk, City>)chunkToCityMap).putIfAbsent(chunk, city);
        if (previous != null) {
            return false;  // Another city claimed this chunk in the meantime.
        }

        // Add this chunk to the city's set in city->chunks map.
        // Use computeIfAbsent to create a new set if the city has no chunks yet.
        cityToChunksMap.computeIfAbsent(city, c -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                .add(chunk);

        // Also record the claim in the chunkClaims map for persistence (using world:x:z as key).
        String chunkKey = getChunkKey(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        chunkClaims.put(chunkKey, city.getName());

        return true;
    }

    /**
     * Claims all relevant chunks for a city (e.g., in a radius around the city's center).
     * This method checks for conflicts and only claims chunks if none are already claimed.
     * @param city The City for which to claim surrounding chunks.
     * @return true if all chunks were claimed successfully, false if a conflict (overlap) was found.
     */
    public boolean claimChunksForCity(City city) {
        // These should be your ClaimManager fields:
        // private final Map<Chunk, String> chunkToCityMap = new HashMap<>();
        // private final Map<String, Set<Chunk>> cityToChunksMap = new HashMap<>();

        Set<Chunk> chunksToClaim = new HashSet<>();
        Location center = city.getLocation();
        World world = center.getWorld();
        int centerX = center.getChunk().getX();
        int centerZ = center.getChunk().getZ();

        // Gather all chunks in the claim radius
        for (int dx = -chunkClaimRadius; dx <= chunkClaimRadius; dx++) {
            for (int dz = -chunkClaimRadius; dz <= chunkClaimRadius; dz++) {
                Chunk chunk = null;
                chunk = world.getChunkAt(centerX + dx, centerZ + dz);
                // Check for overlap immediately
                if (chunkToCityMap.containsKey(chunk)) {
                    System.out.println("DEBUG: Chunk at " + chunk + " already claimed by " + chunkToCityMap.get(chunk));
                    return false; // Abort if any chunk is already claimed
                }
                chunksToClaim.add(chunk);
            }
        }

        // No overlaps, claim all chunks for this city
        for (Chunk chunk : chunksToClaim) {
            chunkToCityMap.put(chunk, city);
        }
        cityToChunksMap.put(city, chunksToClaim); // <-- Only once, after loop!



        System.out.println("DEBUG: " + city.getName() + " successfully claimed " + chunksToClaim.size() + " chunks.");
        return true;
    }


    /**
     * Checks if a given chunk is already claimed by any city.
     */
    public boolean isChunkClaimed(Chunk chunk) {
        return chunkToCityMap.containsKey(chunk);
    }

    /**
     * Gets the City that owns the given chunk, or null if unclaimed.
     */
    public City getCityByChunk(Chunk chunk) {
        return chunkToCityMap.get(chunk);
    }

    /**
     * Convenience method to get the City at a given location (by finding the city owning the chunk).
     */
    public City getCityAt(Location location) {
        Chunk chunk = location.getChunk();
        return getCityByChunk(chunk);
    }

    /**
     * Retrieves all chunks claimed by the given city.
     * @return a Set of Chunks that the city has claimed. If none, returns an empty set.
     */
    public Set<Chunk> getChunksForCity(City city) {
        // Use the cityToChunksMap for quick lookup. Return a defensive copy to prevent external modifications.
        Set<Chunk> chunks = cityToChunksMap.get(city);
        if (chunks == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(chunks);
    }

    /**
     * Unclaims all chunks owned by the given city.
     * This frees those chunks so they can be claimed by other cities in the future.
     */
    public void unclaimChunksForCity(City city) {
        // Get all chunks currently claimed by this city.
        Set<Chunk> ownedChunks = cityToChunksMap.get(city);
        if (ownedChunks == null) {
            return;  // City has no claimed chunks or city not found in map.
        }
        // Remove each chunk from the chunkToCityMap and the persistence map.
        for (Chunk chunk : ownedChunks) {
            chunkToCityMap.remove(chunk);
            String chunkKey = getChunkKey(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
            chunkClaims.remove(chunkKey);
        }
        // Finally, remove the city entry from cityToChunksMap.
        cityToChunksMap.remove(city);
    }

    public String getCityNameAtChunk(org.bukkit.Chunk chunk) {
        City city = chunkToCityMap.get(chunk);
        return city != null ? city.getName() : null;
    }


    /**
     * Utility method to form a unique string key for a chunk based on world and coordinates.
     */
    private String getChunkKey(String worldName, int x, int z) {
        return worldName + ":" + x + ":" + z;
    }
}
