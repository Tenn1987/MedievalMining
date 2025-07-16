package com.brandon.globaleconomy.npc.traits;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.npc.NPCNationManager;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;

public class WorkerTrait extends Trait {

    private static final boolean DEBUG_MODE = false;

    private Worker worker;
    private City city;

    public WorkerTrait() {
        super("WorkerTrait");
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public void run() {
        if (!npc.isSpawned()) return;

        if (DEBUG_MODE) {
            Bukkit.getLogger().info("[DEBUG] WorkerTrait.run() fired.");
            Bukkit.getLogger().info("[DEBUG] NPC is spawned: " + npc.getFullName());
        }

        if (worker == null || city == null) {
            if (DEBUG_MODE) {
                Bukkit.getLogger().warning("[DEBUG] Skipping performWork due to null worker or city.");
            }
            return;
        }

        try {
            worker.performWork(city);
        } catch (Exception e) {
            if (DEBUG_MODE) {
                Bukkit.getLogger().warning("[WorkerTrait] Error during performWork: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Worker getWorker() {
        return worker;
    }

    public City getCity() {
        return city;
    }

    public void assignFromManager(NPC npc) {
        this.worker = NPCNationManager.getInstance().getWorkerByNPC(npc);
        this.city = NPCNationManager.getInstance().getCityByNPC(npc);
    }
}
