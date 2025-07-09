package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.economy.market.MarketAPI;
import com.brandon.globaleconomy.economy.market.MarketItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Farmer extends Worker {
    private static final List<Material> CROPS = List.of(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.PUMPKIN,
            Material.MELON
    );

    public Farmer(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.FARMER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        Material crop = getBestCrop(city);
        if (crop == null) return;

        // Simulate harvest (amount depends on RNG and abundance)
        int harvested = new Random().nextInt(2, 6);
        int kept = 1; // Keep 1 for reseeding
        int sold = harvested - kept;

        // NEW: Use CityProductionManager to handle production rules
        boolean success = city.getProductionManager().produce(this, crop.name(), sold);
        if (!success) {
            Bukkit.getLogger().info(name + " was blocked from producing " + sold + "x " + crop);
            return;
        }

        // Add to city inventory (already validated by produce)
        city.addItem(crop, sold);

        // Get dynamic price and deposit earnings
        MarketItem item = MarketAPI.getInstance().getItem(crop);
        if (item != null) {
            double price = item.getCurrentPrice(); // City buys from farmer
            double earnings = price * sold;
            city.depositToTreasury(city.getEffectiveCurrency(null), earnings);
            Bukkit.getLogger().info(name + " harvested " + sold + "x " + crop + " and earned " + earnings);
        }

        markCooldown();
    }


    private Material getBestCrop(City city) {
        Map<String, Integer> scanned = city.getResources();
        return CROPS.stream()
                .filter(mat -> scanned.getOrDefault(mat.name(), 0) > 0)
                .findFirst()
                .orElse(null);
    }

    public static Farmer farmer(City city, String name, UUID npcId) {
        return new Farmer(city, name, npcId);
    }
}
