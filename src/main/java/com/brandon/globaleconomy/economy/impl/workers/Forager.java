package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Forager extends Worker {
    public Forager(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.FORAGER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        // Full set of log types
        List<String> FORAGEABLES = List.of(
                "SWEET_BERRIES", "GLOW_BERRIES", "MOSS_BLOCK", "AZALEA", "FLOWERING_AZALEA",
                "BAMBOO", "SUGAR_CANE", "CACTUS", "PUMPKIN", "MELON", "COCOA", "KELP", "SEAGRASS"
        );

        Map<String, Integer> scanned = city.getResources();
        List<String> available = FORAGEABLES.stream()
                .filter(scanned::containsKey)
                .filter(k -> scanned.get(k) > 0)
                .toList();

        if (available.isEmpty()) return;

        String chosenLog = available.get(new Random().nextInt(available.size()));
        int amount = 3 + new Random().nextInt(3); // 3–5 logs

        // Update memory
        scanned.put(chosenLog, scanned.getOrDefault(chosenLog, 0) + amount);

        // Chest drop
        Location chestLoc = city.getChestLocation();
        if (chestLoc != null && chestLoc.getBlock().getState() instanceof Chest chest) {
            Material mat = Material.getMaterial(chosenLog);
            if (mat != null) chest.getInventory().addItem(new ItemStack(mat, amount));
        }

        Bukkit.getLogger().info(getName() + " foraged " + amount + " of " + chosenLog + " for " + city.getName());
    }
}
