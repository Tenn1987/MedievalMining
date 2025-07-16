package com.brandon.globaleconomy.npc.impl;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.npc.traits.WorkerTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class NPCSpawner {

    // Method to spawn a worker NPC and add the WorkerTrait
    public static void spawnWorkerNpc(Worker worker, Location spawnLocation) {
        if (worker == null || worker.getCity() == null) {
            Bukkit.getLogger().warning("[NPCSpawner] Worker or City is null for " + (worker != null ? worker.getName() : "null"));
            return;
        }

        String name = worker.getName();
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(spawnLocation);

        WorkerTrait trait = npc.getOrAddTrait(WorkerTrait.class);
        trait.setWorker(worker);

        Bukkit.getLogger().info("[NPCSpawner] Spawned " + name + " at " +
                spawnLocation.getWorld().getName() + " " +
                spawnLocation.getBlockX() + "," +
                spawnLocation.getBlockY() + "," +
                spawnLocation.getBlockZ());
    }

}
