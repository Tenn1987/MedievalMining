package com.brandon.globaleconomy.npc.traits;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import com.brandon.globaleconomy.util.DebugConfig;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BuilderTrait extends Trait {
    private Worker builderWorker;
    private City city;

    public BuilderTrait() {
        super("buildertrait");
    }

    @Override
    public void onAttach() {
        Bukkit.getLogger().info("[BuilderTrait] Attached to NPC: " + npc.getName());
    }

    @Override
    public void run() {
        if (npc == null || !npc.isSpawned()) return;
        if (builderWorker == null) return;
        try {
            builderWorker.performWork(city);
        } catch (Exception e) {
            Bukkit.getLogger().warning("[BuilderTrait] Error during performWork: " + e.getMessage());
            if (DebugConfig.DEBUG_MODE) e.printStackTrace();
        }
    }

    public void assign(Worker worker, City city) {
        this.builderWorker = worker;
        this.city = city;
    }
}
