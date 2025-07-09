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

public class Digger extends Worker {
    public Digger(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.DIGGER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        List<String> diggables = List.of("CLAY", "MUD", "DRIPSTONE_BLOCK", "GRAVEL", "SAND");
        Map<String, Integer> scanned = city.getResources();

        List<String> available = diggables.stream()
                .filter(scanned::containsKey)
                .filter(k -> scanned.get(k) > 0)
                .toList();

        if (available.isEmpty()) return;

        String chosen = available.get(new Random().nextInt(available.size()));
        int amount = 3 + new Random().nextInt(3);

        // Update memory
        scanned.put(chosen, scanned.getOrDefault(chosen, 0) + amount);

        // Chest deposit
        Location chestLoc = city.getChestLocation();
        if (chestLoc != null && chestLoc.getBlock().getState() instanceof Chest chest) {
            Material output = chosen.equals("CLAY") ? Material.CLAY_BALL : Material.getMaterial(chosen);
            int yield = chosen.equals("CLAY") ? amount * 4 : amount;

            if (output != null) {
                chest.getInventory().addItem(new ItemStack(output, yield));
            }
        }

        Bukkit.getLogger().info(getName() + " dug " + amount + " " + chosen + " for " + city.getName());
    }
}