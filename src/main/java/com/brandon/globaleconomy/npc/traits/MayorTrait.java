package com.brandon.globaleconomy.npc.traits;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.core.PluginCore;
import com.brandon.globaleconomy.economy.impl.workers.*;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import com.brandon.globaleconomy.economy.impl.workers.WorkerManager;
import com.brandon.globaleconomy.util.DebugConfig;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;

public class MayorTrait extends Trait {

    private City city;

    public MayorTrait() {
        super("mayortrait");
    }

    @Override
    public void run() {
        if (npc == null || !npc.isSpawned()) return;

        if (city == null) {
            CityManager cityManager = PluginCore.getInstance().getCityManager();
            city = cityManager.getCityAt(npc.getStoredLocation());
        }

        if (city == null) return;

        for (NPC candidate : city.getUnemployedNPCs()) {
            if (candidate == null || !candidate.isSpawned()) continue;

            @SuppressWarnings("deprecation")
            WorkerTrait trait = candidate.getTrait(WorkerTrait.class);

            if (trait.getWorker() != null && trait.getWorker().getRole() != WorkerRole.RESIDENT)
                continue;

            Worker assigned = assignBasedOnNeed(city, candidate.getName(), candidate.getUniqueId());

            if (assigned != null) {
                trait.setWorker(assigned);
                WorkerManager.getInstance().registerWorker(assigned);
                candidate.setName(assigned.getName() + " (" + assigned.getRole().name() + ")");

                if (DebugConfig.SHOW_WORKER_TASKS) {
                    Bukkit.getLogger().info("[MayorTrait] Assigned " + assigned.getRole().name() + " to " + assigned.getName());
                }
                break; // Assign one NPC per run cycle
            }
        }
    }

    private Worker assignBasedOnNeed(City city, String name, java.util.UUID uuid) {
        long farmers = city.getWorkers().stream().filter(w -> w.getRole() == WorkerRole.FARMER).count();
        long miners = city.getWorkers().stream().filter(w -> w.getRole() == WorkerRole.MINER).count();
        long merchants = city.getWorkers().stream().filter(w -> w.getRole() == WorkerRole.MERCHANT).count();
        long guards = city.getWorkers().stream().filter(w -> w.getRole() == WorkerRole.GUARD).count();
        long builders = city.getWorkers().stream().filter(w -> w.getRole() == WorkerRole.BUILDER).count();

        if (farmers < 2) return new Farmer(city, name, uuid);
        if (miners < 2) return new Miner(city, name, uuid);
        if (merchants < 1) return new Merchant(city, name, uuid);
        if (guards < 1) return new Guard(city, name, uuid);
        if (builders < 1) return new Builder(city, name, uuid);

        return new Farmer(city, name, uuid); // Fallback
    }
}
