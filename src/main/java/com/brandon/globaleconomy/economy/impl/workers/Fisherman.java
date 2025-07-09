package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Fisherman extends Worker {
    public Fisherman(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.FISHERMAN, npcId);
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        List<String> FISH = List.of("COD", "SALMON", "PUFFERFISH", "TROPICAL_FISH");
        Map<String, Integer> scanned = city.getResources();

        List<String> available = FISH.stream()
                .filter(scanned::containsKey)
                .filter(k -> scanned.get(k) > 0)
                .toList();

        if (available.isEmpty()) return;

        String chosenFish = available.get(new Random().nextInt(available.size()));
        int amount = 3 + new Random().nextInt(3); // 3–5

        if (!city.getProductionManager().produce(this, chosenFish, amount)) {
            Bukkit.getLogger().info(getName() + " was blocked from fishing " + amount + "x " + chosenFish);
            return;
        }

        Material mat = Material.getMaterial(chosenFish);
        if (mat == null) return;

        city.addItem(mat, amount); // Add to city inventory

        // Chest drop
        Location chestLoc = city.getChestLocation();
        if (chestLoc != null && chestLoc.getBlock().getState() instanceof Chest chest) {
            chest.getInventory().addItem(new ItemStack(mat, amount));
        }

        Bukkit.getLogger().info(getName() + " fished " + amount + "x " + chosenFish + " for " + city.getName());

        markCooldown();
    }
}
