package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityProductionManager;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class Poleturner extends Worker {
    public Poleturner(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.POLETURNER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        CityProductionManager manager = city.getProductionManager();

        List<String> logs = List.of(
                "OAK_LOG", "BIRCH_LOG", "SPRUCE_LOG", "JUNGLE_LOG",
                "ACACIA_LOG", "DARK_OAK_LOG", "MANGROVE_LOG", "CHERRY_LOG"
        );

        String availableLog = logs.stream()
                .filter(log -> city.getResources().getOrDefault(log, 0) >= 1)
                .findAny()
                .orElse(null);

        if (availableLog == null) return;

        if (!manager.canProduce(this, "STICK", 8)) {
            Bukkit.getLogger().info(getName() + " was blocked from making sticks.");
            return;
        }

        manager.consume(availableLog, 1);
        manager.recordProduction("STICK", 8);
        city.addItem(org.bukkit.Material.STICK, 8);

        logWork("converted 1x " + availableLog + " into 8 sticks.");
        markCooldown();
    }
}
