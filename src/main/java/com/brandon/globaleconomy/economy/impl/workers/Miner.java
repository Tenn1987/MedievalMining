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

public class Miner extends Worker {
    public Miner(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.MINER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        List<String> ORES = List.of(
                "COAL_ORE", "IRON_ORE", "COPPER_ORE", "GOLD_ORE",
                "LAPIS_ORE", "REDSTONE_ORE", "DIAMOND_ORE",
                "STONE", "DIORITE", "GRANITE", "SANDSTONE"
        );

        Map<String, Integer> scanned = city.getResources();

        List<String> available = ORES.stream()
                .filter(scanned::containsKey)
                .filter(k -> scanned.get(k) > 0)
                .toList();

        if (available.isEmpty()) return;

        String chosenOre = available.get(new Random().nextInt(available.size()));
        int amount = 3 + new Random().nextInt(3); // 3–5

        if (!city.getProductionManager().produce(this, chosenOre, amount)) {
            Bukkit.getLogger().info(name + " was blocked from mining " + amount + "x " + chosenOre);
            return;
        }

        Material drop = switch (chosenOre) {
            case "STONE" -> Material.COBBLESTONE;
            case "COAL_ORE" -> Material.COAL;
            case "IRON_ORE" -> Material.RAW_IRON;
            case "COPPER_ORE" -> Material.RAW_COPPER;
            case "GOLD_ORE" -> Material.RAW_GOLD;
            case "LAPIS_ORE" -> Material.LAPIS_LAZULI;
            case "REDSTONE_ORE" -> Material.REDSTONE;
            case "DIAMOND_ORE" -> Material.DIAMOND;
            default -> Material.valueOf(chosenOre); // fallback: cobble/stone types
        };

// Bonus drop range for ores that yield more per block
        int adjustedAmount = switch (chosenOre) {
            case "LAPIS_ORE" -> amount * (4 + new Random().nextInt(6));     // 4–9
            case "REDSTONE_ORE" -> amount * (4 + new Random().nextInt(2));  // 4–5
            default -> amount;
        };

        city.addItem(drop, adjustedAmount);

// Chest drop (optional)
        Location chestLoc = city.getChestLocation();
        if (chestLoc != null && chestLoc.getBlock().getState() instanceof Chest chest) {
            chest.getBlockInventory().addItem(new ItemStack(drop, adjustedAmount));
        }

        markCooldown();
    }
}