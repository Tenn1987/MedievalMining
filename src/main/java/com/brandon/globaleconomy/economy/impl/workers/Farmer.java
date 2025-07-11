package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.core.PluginCore;
import com.brandon.globaleconomy.economy.market.MarketAPI;
import com.brandon.globaleconomy.economy.market.MarketItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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

    private long lastWorkTime = 0;
    private static final long COOLDOWN_MS = 15000; // 15 seconds

    public Farmer(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.FARMER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWorkTime < COOLDOWN_MS) return;
        lastWorkTime = System.currentTimeMillis();

        Material crop = getBestCrop(city);
        if (crop == null) return;

        int harvested = new Random().nextInt(2, 6);
        int kept = 1;
        int sold = harvested - kept;

        // Delay execution by 3 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean success = city.getProductionManager().produce(Farmer.this, crop.name(), sold);
                if (!success) {
                    Bukkit.getLogger().info(name + " was blocked from producing " + sold + "x " + crop);
                    return;
                }

                city.addItem(crop, sold);

                MarketItem item = MarketAPI.getInstance().getItem(crop);
                if (item != null) {
                    double price = item.getCurrentPrice();
                    double earnings = price * sold;
                    city.depositToTreasury(city.getEffectiveCurrency(null), earnings);
                    Bukkit.getLogger().info(name + " harvested " + sold + "x " + crop + " and earned " + earnings);
                }

                markCooldown();
            }
        }.runTaskLater(PluginCore.getInstance(), 60L); // 3 seconds (60 ticks)
    }

    private Material getBestCrop(City city) {
        Map<String, Integer> scanned = city.getResources();
        return CROPS.stream()
                .filter(mat -> scanned.getOrDefault(mat.name(), 0) > 0)
                .findFirst()
                .orElse(null);
    }
}
