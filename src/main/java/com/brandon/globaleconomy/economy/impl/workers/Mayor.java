package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.npc.impl.WorkerTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class Mayor extends Worker {
    private static final Set<WorkerRole> unemployedRoles = EnumSet.of(WorkerRole.RESIDENT);

    public Mayor(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.MAYOR, npcId);
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        Bukkit.getLogger().info("Mayor " + name + " is checking NPCs...");
        Bukkit.getLogger().info("Inventory snapshot: " + city.getAllCityInventory());

        Iterable<NPC> npcs = CitizensAPI.getNPCRegistry();

        for (NPC npc : npcs) {
            Location loc = npc.getStoredLocation();
            if (loc == null || !loc.getWorld().equals(city.getLocation().getWorld())) continue;
            if (loc.distance(city.getLocation()) > 40) continue;
            if (!npc.hasTrait(WorkerTrait.class)) continue;

            WorkerTrait trait = npc.getTrait(WorkerTrait.class);
            Worker worker = trait.getWorker();

            if (worker == null || isUnemployed(worker)) {
                Worker newWorker = autoAssignBasedOnInventory(city, npc);
                if (newWorker != null) {
                    trait.setWorker(newWorker);
                    WorkerManager.getInstance().registerWorker(newWorker);
                    Bukkit.getLogger().info(name + " assigned " + newWorker.getRole() + " to " + npc.getName());
                    break;
                }
            }
        }

        markCooldown();
    }

    private boolean isUnemployed(Worker worker) {
        return unemployedRoles.contains(worker.getRole());
    }

    private Worker autoAssignBasedOnInventory(City city, NPC npc) {
        Bukkit.getLogger().info("City inventory: " + city.getAllCityInventory());

        if (city.hasItem(Material.IRON_PICKAXE)) {
            city.takeItem(Material.IRON_PICKAXE, 1);
            return new Miner(city, npc.getName(), npc.getUniqueId());
        }

        if (city.hasItem(Material.IRON_AXE)) {
            city.takeItem(Material.IRON_AXE, 1);
            return new Woodsman(city, npc.getName(), npc.getUniqueId());
        }

        if (city.hasItem(Material.FISHING_ROD)) {
            city.takeItem(Material.FISHING_ROD, 1);
            return new Fisherman(city, npc.getName(), npc.getUniqueId());
        }

        if (city.hasItem(Material.IRON_HOE)) {
            city.takeItem(Material.IRON_HOE, 1);
            return new Farmer(city, npc.getName(), npc.getUniqueId());
        }

        if (city.hasItem(Material.STICK)) {
            city.takeItem(Material.STICK, 1);
            return new Builder(city, npc.getName(), npc.getUniqueId());
        }

        return null;
    }
}
