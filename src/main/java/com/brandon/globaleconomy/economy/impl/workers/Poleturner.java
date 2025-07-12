package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityProductionManager;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class Poleturner extends Worker {
    public Poleturner(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.POLETURNER, npcId);
    }

    @Override
    public WorkerRole getRole() {
        return WorkerRole.POLETURNER;
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        CityProductionManager manager = city.getProductionManager();

        List<String> logs = List.of(
                "OAK_LOG", "BIRCH_LOG", "SPRUCE_LOG", "JUNGLE_LOG",
                "ACACIA_LOG", "DARK_OAK_LOG", "MANGROVE_LOG", "CHERRY_LOG",
                "DEAD_BUSH" // desert support
        );

        String availableLog = logs.stream()
                .filter(log -> city.getResources().getOrDefault(log, 0) >= 1)
                .findAny()
                .orElse(null);

        if (availableLog == null) return;

        // Use reduced output if using DEAD_BUSH
        int outputSticks = 8;

        if (!manager.canProduce(this, "STICK", outputSticks)) {
            city.log("§e[POLETURNER] " + getName() + " was blocked from making sticks due to production limits.");
            return;
        }


        manager.consume(availableLog, 1);
        manager.recordProduction("STICK", outputSticks);
        city.addItem(Material.STICK, outputSticks);

        rateLimitedLog(getName(), "Poleturner " + getName() + " crafted " + outputSticks + " sticks for " + city.getName(), 5000);
        markCooldown();
    }
}
