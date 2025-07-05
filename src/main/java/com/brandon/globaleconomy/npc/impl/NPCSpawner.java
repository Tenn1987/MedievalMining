package com.brandon.globaleconomy.npc.impl;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.npc.impl.WorkerTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class NPCSpawner {

    public static void spawnWorkerNpc(Worker worker, Location location) {
        City city = worker.getCity();
        String name = worker.getName();

        // Offset from bell: 1 block east, 1 block up, 1 block south
        Location bellLocation = city.getLocation();
        Location spawnLocation = bellLocation.clone().add(1, 1, 1);

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(spawnLocation);

        WorkerTrait trait = npc.getOrAddTrait(WorkerTrait.class);
        if (trait != null) {
            trait.setWorker(worker);
        } else {
            Bukkit.getLogger().warning("[NPCSpawner] WorkerTrait was null for NPC: " + name);
        }
    }
}
